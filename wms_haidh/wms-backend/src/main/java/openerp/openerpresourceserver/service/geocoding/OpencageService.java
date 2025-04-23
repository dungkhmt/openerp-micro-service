package openerp.openerpresourceserver.service.geocoding;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import openerp.openerpresourceserver.dto.request.CoordinateDTO;
import openerp.openerpresourceserver.dto.response.AddressResponse;

@Service
@Profile("opencage")
public class OpencageService implements GeocodingService {

    @Value("${opencage.url}")
    private String apiUrl;

    @Value("${opencage.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpencageService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public AddressResponse getAddressFromCoordinates(CoordinateDTO coordinates) {
        String uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("key", apiKey)
                .queryParam("q", coordinates.getLat() + "," + coordinates.getLng())
                .queryParam("pretty", 1)
                .toUriString();

        String response = restTemplate.getForObject(uri, String.class);

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode results = root.path("results");
            if (results.isArray() && results.size() > 0) {
                String formatted = results.get(0).path("formatted").asText();
                return new AddressResponse(formatted);
            } else {
                return new AddressResponse("No address found for given coordinates.");
            }
        } catch (Exception e) {
            // Log lỗi nếu cần
            return new AddressResponse("Failed to parse response from geocoding API.");
        }
    }
}
