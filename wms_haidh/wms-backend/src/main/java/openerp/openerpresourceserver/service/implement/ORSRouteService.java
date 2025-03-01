package openerp.openerpresourceserver.service.implement;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.service.RouteService;

@Service("orsRouteService")
@AllArgsConstructor
public class ORSRouteService implements RouteService {

	private static final String ORS_API_KEY = "5b3ce3597851110001cf62482bb93e451660484e863c1ab19cc3ee2c";
	private static final String ORS_URL = "https://api.openrouteservice.org/v2/directions/driving-car/geojson";

	private final RestTemplate restTemplate;

	@Override
	public ResponseEntity<String> fetchRoute(Map<String, Object> requestBody) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", ORS_API_KEY);

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

		try {
			return restTemplate.exchange(ORS_URL, HttpMethod.POST, entity, String.class);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching route");
		}
	}


}
