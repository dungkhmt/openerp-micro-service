package openerp.openerpresourceserver.service.implement;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import openerp.openerpresourceserver.service.RouteService;

@Service("orsRouteService")
public class ORSRouteService implements RouteService {

    @Value("${ors.api-key}")
    private String apiKey;

    @Value("${ors.url}")
    private String orsUrl;

    private final RestTemplate restTemplate;
    
    @Autowired
    public ORSRouteService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ResponseEntity<String> fetchRoute(Map<String, Object> requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            return restTemplate.exchange(orsUrl, HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching route");
        }
    }
}

