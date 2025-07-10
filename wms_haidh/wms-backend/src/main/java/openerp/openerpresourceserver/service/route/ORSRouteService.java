package openerp.openerpresourceserver.service.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.CoordinateDTO;
import openerp.openerpresourceserver.dto.request.CoordinateRequest;
import openerp.openerpresourceserver.dto.response.RoutePathResponse;

@Service
@Profile("ors")
@RequiredArgsConstructor
public class ORSRouteService implements RouteService {

	private final RestTemplate restTemplate;

	@Value("${ors.api-key}")
	private String apiKey;

	@Value("${ors.url}")
	private String orsUrl;
	
	@Value("${ors.matrix-url}")
	private String orsMatrixUrl;

	@Override
	public RoutePathResponse getRoute(CoordinateRequest request) {
		try {

			List<List<Double>> coords = request.getCoordinates().stream()
				.map(c -> List.of(c.getLng(), c.getLat()))
				.collect(Collectors.toList());

			Map<String, Object> requestBody = new HashMap<>();
			requestBody.put("coordinates", coords);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", apiKey);

			HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

			ResponseEntity<JsonNode> response = restTemplate.exchange(
				orsUrl, HttpMethod.POST, requestEntity, JsonNode.class
			);

			JsonNode body = response.getBody();

			if (body == null || !body.has("features")) {
				throw new IllegalStateException("ORS response invalid or empty");
			}

			JsonNode feature = body.get("features").get(0);
			double distance = feature.get("properties").get("summary").get("distance").asDouble();

			List<List<Double>> path = new ArrayList<>();
			JsonNode coordinatesNode = feature.get("geometry").get("coordinates");

			for (JsonNode coord : coordinatesNode) {
				List<Double> point = new ArrayList<>();
				point.add(coord.get(1).asDouble()); 
				point.add(coord.get(0).asDouble()); 
				path.add(point); 
			}

			return RoutePathResponse.builder()
				.distance(distance)
				.path(path)
				.build();

		} catch (Exception e) {
			throw new RuntimeException("Failed to fetch route from ORS", e);
		}
	}

	@Override
	public double[][] getDistances(List<CoordinateDTO> coordinateList) {
		try {
			List<List<Double>> coords = coordinateList.stream()
				.map(c -> List.of(c.getLng(), c.getLat()))
				.collect(Collectors.toList());

			Map<String, Object> requestBody = new HashMap<>();
			requestBody.put("locations", coords);
			requestBody.put("metrics", List.of("distance"));
			requestBody.put("units", "m");

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", apiKey);

			HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

			ResponseEntity<JsonNode> response = restTemplate.exchange(
				orsMatrixUrl, HttpMethod.POST, requestEntity, JsonNode.class
			);

			JsonNode body = response.getBody();
			if (body == null || !body.has("distances")) {
				throw new IllegalStateException("ORS matrix response invalid or missing 'distances'");
			}

			JsonNode distancesNode = body.get("distances");
			double[][] distances = new double[distancesNode.size()][];

			for (int i = 0; i < distancesNode.size(); i++) {
				JsonNode row = distancesNode.get(i);
				distances[i] = new double[row.size()];
				for (int j = 0; j < row.size(); j++) {
					distances[i][j] = row.get(j).asDouble();
				}
			}

			return distances;

		} catch (Exception e) {
			throw new RuntimeException("Failed to fetch distance matrix from ORS", e);
		}
	}


}
