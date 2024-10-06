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
import java.util.Arrays;
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

public class GeolocationResolver {

  private static final int MAX_REQUESTS_PER_MINUTE = 40; // ORS has a cap of 40 requests per min
  private final String LOCATION_RESOLVER_API_URL;
  private final String ROUTE_RESOLVER_API_URL;
  private final String API_KEY;
  private final HttpClient client = HttpClient.newHttpClient();
  private final Logger log = LogManager.getLogger(getClass());

  public GeolocationResolver() {
    Dotenv dotenv = Dotenv.load();
    API_KEY = dotenv.get("ORS_API_KEY");
    LOCATION_RESOLVER_API_URL = "https://api.openrouteservice.org/geocode/search?api_key=" + API_KEY
        + "&boundary.country=NZ&size=1";
    ROUTE_RESOLVER_API_URL = "https://api.openrouteservice.org/v2/directions/driving-car";
  }

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

  public CompletableFuture<GeoLocation> resolveLocation(String locationName) {
    Timer timer = new Timer();
    String encodedLocationName = URLEncoder.encode(locationName, StandardCharsets.UTF_8);
    String url = LOCATION_RESOLVER_API_URL + "&text=" + encodedLocationName;
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
//          .orTimeout(3, TimeUnit.SECONDS)
          .exceptionally(error -> {
            log.error("Encountered an error sending location resolver request", error);
            throw new CompletionException(error); // Propagate the error
          });
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

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
            timer.stop());
        return new GeoLocation(lat, lon);
      }
      log.error("Could not resolve location with search string '{}' in {}ms", locationName,
          timer.stop());
    } catch (Exception e) {
      log.error("Failed to resolve location with search string '{}'", locationName, e);
    }
    return null;
  }

  public String getRoute(List<GeoLocation> vineyards) {
    String geometry;

    JSONArray coordinatesArray = new JSONArray();

    for (GeoLocation vineyard : vineyards) {
      JSONArray coordinate = new JSONArray();
      coordinate.add(vineyard.getLongitude());
      coordinate.add(vineyard.getLatitude());

      coordinatesArray.add(coordinate);
    }

    JSONObject routeBody = new JSONObject();
    routeBody.put("coordinates", coordinatesArray);

    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(new URI(ROUTE_RESOLVER_API_URL))
          .header("Authorization", API_KEY)
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(routeBody.toString(), StandardCharsets.UTF_8))
          .build();

      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


      JSONParser parser = new JSONParser();
      JSONObject parsedRespone = (JSONObject) parser.parse(response.body());

      JSONArray routesArray = (JSONArray) parsedRespone.get("routes");

      JSONObject firstRoute = (JSONObject) routesArray.get(0);

      geometry = (String) firstRoute.get("geometry");

    } catch (URISyntaxException | IOException | InterruptedException | ParseException e) {
      throw new RuntimeException(e);
    }

    if (geometry != null) {
      return geometry;
    } else {
      throw new RuntimeException("error");
    }

  }

//  public static void main(String[] args) {
//    GeolocationResolver geolocationResolver = new GeolocationResolver();
//    String route = geolocationResolver.getRoute(Arrays.asList(
//            new GeoLocation(-43.522442, 172.580683),
//            new GeoLocation(-43.530542, 172.626466),
//            new GeoLocation(-43.52556, 172.57944),
//            new GeoLocation(-43.52907, 172.60660)
//    ));
//  }

}
