package seng202.team0.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import seng202.team0.database.GeoLocation;

public class LocationResolverService {
  private static final String LOCATION_RESOLVER_API_URL = "https://nominatim.openstreetmap.org/search.php";
  private final HttpClient client = HttpClient.newHttpClient();
  private final Logger log = LogManager.getLogger(getClass());
  private final JSONParser jsonParser = new JSONParser();

  public CompletableFuture<GeoLocation> resolveLocation(String locationName) {
    String encodedLocationName = URLEncoder.encode(locationName, StandardCharsets.UTF_8);
    String url = LOCATION_RESOLVER_API_URL + "?q=" + encodedLocationName + "&format=json&limit=1&countrycodes=nz";
    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(new URI(url))
          .header("User-Agent", "Java 21 Http Client")
          .GET()
          .build();

      return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
          .thenApply(HttpResponse::body)
          .thenApply(this::parseResponse)
          .orTimeout(1, TimeUnit.SECONDS)
          .exceptionally(error -> {
            log.error("Encountered an error sending location resolver request", error);
            return null;
          });
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  private GeoLocation parseResponse(String responseBody) {
    try {
      JSONArray results = (JSONArray) jsonParser.parse(responseBody);
      if (!results.isEmpty()) {
        JSONObject firstResult = (JSONObject) results.getFirst();
        double lat = Double.parseDouble((String) firstResult.get("lat"));
        double lon = Double.parseDouble((String) firstResult.get("lon"));
        return new GeoLocation(lat, lon);
      }
      log.error("Location not found");
      return null;
    } catch (Exception e) {
      throw new RuntimeException("Error parsing response", e);
    }
  }

  // for testing

  private static final LocationResolverService locationResolver = new LocationResolverService();

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    blockingResolveLocation("Christchurch");
    blockingResolveLocation("Auckland");
    blockingResolveLocation("Hamilton");
    blockingResolveLocation("Wellington");
  }

  public static void blockingResolveLocation(String locationName) {
    var location = locationResolver.resolveLocation(locationName).join();
    System.out.println(locationName + " Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());
  }
}
