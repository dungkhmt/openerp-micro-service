package wms.algorithms.utils;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wms.algorithms.entity.DistanceMatrix;
import wms.algorithms.entity.Node;
import wms.common.CommonResource;

import java.io.IOException;

@Component
public class Utils {
    public static String graphhopperUrl;
    public static String apiKey;
    // https://mkyong.com/spring/spring-inject-a-value-into-static-variables/
    @Value("${graphhopper.url}")
    public void setUrl(String url) {
        graphhopperUrl = url;
    }
    @Value("${graphhopper.apiKey}")
    public void setKey(String key) {
        apiKey = key;
    }
    public static double calculateEuclideanDistance(Node n1, Node n2) {
        double dx = n1.getX() - n2.getX();
        double dy = n1.getY() - n2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
    public static double calculateCoordinationDistance(double lat1, double lon1, double lat2, double lon2) {
        double r = 6371; // radius of the earth in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = r * c;

        return distance * 1000;
    }
    public static double getDistanceGraphhopperApi(double sourceLat, double sourceLon, double targetLat, double targetLon) throws IOException {
        String apiUrl = String.format(graphhopperUrl,
                sourceLat, sourceLon, targetLat, targetLon, apiKey);
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(apiUrl);
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        entity.getContent();
        String jsonResult = EntityUtils.toString(entity);
        Gson gson = new Gson();
        DistanceMatrix distanceMatrix = gson.fromJson(jsonResult, DistanceMatrix.class);
        return distanceMatrix.getPaths().size() > 0 ? distanceMatrix.getPaths().get(0).getDistance() : -1.0;
//        return -1; // Return a negative value to indicate failure
    }
}