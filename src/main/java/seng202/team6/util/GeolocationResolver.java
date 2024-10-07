package seng202.team6.util;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import seng202.team6.model.GeoLocation;

/**
 * The GeolocationResolver class is responsible for resolving geographical coordinates for a
 * list of locations, as well as finding driving routes between geographical points.
 * It interacts with the OpenRouteService (ORS) API to perform geocoding and routing requests.
 */
public class GeolocationResolver {

  private static final int MAX_REQUESTS_PER_MINUTE = 100; // ORS has a cap of 100 requests per min
  private final String locationResolverApiUrl;
  private final String routeResolverApiUrl;
  private final String apiKey;
  private final HttpClient client = HttpClient.newHttpClient();
  private final Logger log = LogManager.getLogger(getClass());

  /**
   * Constructs a new GeolocationResolver. Loads the ORS API key from environment variables.
   * Initializes the URLs for both geolocation and route resolution.
   */
  public GeolocationResolver() {
    Dotenv dotenv = Dotenv.load();
    apiKey = dotenv.get("ORS_API_KEY");
    locationResolverApiUrl = "https://api.openrouteservice.org/geocode/search?api_key=" + apiKey
        + "&boundary.country=NZ&size=1";
    routeResolverApiUrl = "https://api.openrouteservice.org/v2/directions/driving-car";
  }

  /**
   * Resolves the geographical coordinates of a list of location names. This method splits the
   * locations into batches of requests, ensuring compliance with the rate limits of the ORS API.
   * It performs these requests asynchronously.
   *
   * @param locations a list of location names to be geocoded.
   * @return a map of location names to their respective GeoLocation objects.
   */
  public Map<String, GeoLocation> resolveAll(List<String> locations) {
    Map<String, CompletableFuture<GeoLocation>> futuresMap = new ConcurrentHashMap<>();

    int totalRequests = locations.size();
    List<CompletableFuture<Void>> allBatches = new ArrayList<>();

    for (int i = 0; i < totalRequests; i += MAX_REQUESTS_PER_MINUTE) {
      List<String> batch = locations.subList(i,
          Math.min(i + MAX_REQUESTS_PER_MINUTE, totalRequests));

      batch.forEach(location -> {
        CompletableFuture<GeoLocation> future = resolveLocation(location);
        futuresMap.put(location, future);
      });

      // collect all futures in this batch
      CompletableFuture<Void> batchFuture = CompletableFuture.allOf(
          batch.stream().map(futuresMap::get).toArray(CompletableFuture[]::new)
      );
      allBatches.add(batchFuture);

      // throttle by waiting for 60 seconds between batches
      // not really any way around this
      if (i + MAX_REQUESTS_PER_MINUTE < totalRequests) {
        batchFuture.thenRun(() -> {
          try {
            TimeUnit.SECONDS.sleep(60); // wait for 60 seconds
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        }).join();
      }
    }

    // wait for all batches to complete
    CompletableFuture<Void> allFutures = CompletableFuture.allOf(
        allBatches.toArray(new CompletableFuture[0])
    );
    allFutures.join();

    // Collect results into a Map<String, GeoLocation>
    return futuresMap.entrySet().stream()
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> entry.getValue().join(),
            (e1, e2) -> e1,  // handle key collisions if any
            ConcurrentHashMap::new
        ));
  }

  /**
   * Resolves the geographical coordinates of a single location asynchronously. The method sends an
   * HTTP request to the ORS geocoding API and parses the response to extract the coordinates.
   *
   * @param locationName the name of the location to be geocoded.
   * @return a CompletableFuture containing the GeoLocation object for the resolved location.
   */
  public CompletableFuture<GeoLocation> resolveLocation(String locationName) {
    Timer timer = new Timer();
    String encodedLocationName = URLEncoder.encode(locationName, StandardCharsets.UTF_8);
    String url = locationResolverApiUrl + "&text=" + encodedLocationName;
    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(new URI(url))
          .header("User-Agent", "Java 21 Http Client")
          .header("content-type", "application/json")
          .GET()
          .build();

      return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
          .thenApply(response -> {
            log.info("Status code for location '{}': {}", locationName, response.statusCode());
            return response.body();
          })
          .thenApply(response -> parseResponse(locationName, response, timer))
          .exceptionally(error -> {
            log.error("Encountered an error sending location resolver request", error);
            throw new CompletionException(error); // Propagate the error
          });
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Parses the geocoding response from the ORS API and extracts the geographical coordinates.
   *
   * @param locationName the name of the location being resolved.
   * @param responseBody the response body from the ORS API.
   * @param timer  a timer object to measure the time taken for the request.
   * @return a GeoLocation object containing the latitude and longitude of the location,
   *        or null if the location could not be resolved.
   */
  private GeoLocation parseResponse(String locationName, String responseBody, Timer timer) {
    JSONParser jsonParser = new JSONParser();
    try {
      JSONObject jsonObject = (JSONObject) jsonParser.parse(responseBody);
      JSONArray features = (JSONArray) jsonObject.get("features");
      if (features != null && !features.isEmpty()) {
        JSONObject firstResult = (JSONObject) features.getFirst();
        JSONObject geometry = (JSONObject) firstResult.get("geometry");
        JSONArray coordinates = (JSONArray) geometry.get("coordinates");
        // for some reason ORS returns the longitude first
        double lon = (double) coordinates.get(0);
        double lat = (double) coordinates.get(1);
        log.info("Successfully resolved geolocation for location '{}' in {}ms", locationName,
            timer.currentOffsetMilliseconds());
        return new GeoLocation(lat, lon);
      }
      log.error("Could not resolve location with search string '{}' in {}ms", locationName,
          timer.currentOffsetMilliseconds());
    } catch (Exception e) {
      log.error("Failed to resolve location with search string '{}'", locationName, e);
    }
    return null;
  }

  /**
   * Resolves a driving route between multiple geographical locations. The method sends a POST
   * request to the ORS routing API and parses the response to return the route geometry.
   *
   * @param vineyardLocations a list of GeoLocation objects representing the vineyard locations.
   * @return the route geometry as a string, or null if no route could be resolved.
   */
  @SuppressWarnings("unchecked")
  public String resolveRoute(List<GeoLocation> vineyardLocations) {
    JSONObject content = new JSONObject();
    JSONArray coordinatesArray = new JSONArray();
    vineyardLocations.forEach(geolocation -> {
      JSONArray coordinates = new JSONArray();
      coordinates.add(geolocation.getLongitude());
      coordinates.add(geolocation.getLatitude());
      coordinatesArray.add(coordinates);
    });
    JSONArray radii = new JSONArray();
    radii.add(-1);
    content.put("coordinates", coordinatesArray);
    content.put("radiuses", radii);

    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(new URI(routeResolverApiUrl))
          .header("Authorization", apiKey)
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(content.toString(), StandardCharsets.UTF_8))
          .build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      String geometricLine = parseRouteResponse(response);
      if (geometricLine != null) {
        log.info("Successfully found route to vineyards");
        return geometricLine;
      }
      log.warn("Could not find route to vineyards");
    } catch (URISyntaxException | IOException | InterruptedException e) {
      log.error("Failed to resolve route to vineyards", e);
    }
    return null;
  }

  /**
   * Parses the route response from the ORS API to extract the route geometry.
   *
   * @param response the HTTP response from the ORS API.
   * @return the route geometry as a string, or null if the request was unsuccessful.
   */
  private String parseRouteResponse(HttpResponse<String> response) {
    if (response.statusCode() != 200) {
      return null;
    }
    JSONParser parser = new JSONParser();
    try {
      JSONObject content = (JSONObject) parser.parse(response.body());
      JSONArray routesArray = (JSONArray) content.get("routes");
      JSONObject firstRoute = (JSONObject) routesArray.getFirst();
      return (String) firstRoute.get("geometry");
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

}
