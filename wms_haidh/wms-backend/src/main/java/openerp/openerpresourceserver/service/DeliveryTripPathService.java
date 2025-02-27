package openerp.openerpresourceserver.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.DeliveryTripPath;
import openerp.openerpresourceserver.entity.projection.DeliveryTripPathProjection;
import openerp.openerpresourceserver.repository.DeliveryTripPathRepository;

@Service
@AllArgsConstructor
public class DeliveryTripPathService {

    private final DeliveryTripPathRepository deliveryTripPathRepository;
    private final RouteService routeService;
    
    public List<DeliveryTripPathProjection> getDeliveryTripPaths(String deliveryTripId) {
        return deliveryTripPathRepository.findByDeliveryTripId(deliveryTripId);
    }

    public void saveRoutePath(String deliveryTripId, List<List<Double>> coordinates) {
        Map<String, Object> requestBody = Map.of("coordinates", coordinates);
        ResponseEntity<String> response = routeService.fetchRoute(requestBody);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                JsonNode responseJson = new ObjectMapper().readTree(response.getBody());
                JsonNode routeCoordinates = responseJson.path("features").get(0).path("geometry").path("coordinates");

                for (JsonNode coord : routeCoordinates) {
                    DeliveryTripPath path = DeliveryTripPath.builder()
                        .deliveryTripId(deliveryTripId)
                        .longitude(coord.get(0).asDouble())
                        .latitude(coord.get(1).asDouble())
                        .createdStamp(LocalDateTime.now())
                        .build();
                    deliveryTripPathRepository.save(path);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

