package openerp.openerpresourceserver.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.request.CoordinateDTO;
import openerp.openerpresourceserver.dto.request.CoordinateRequest;
import openerp.openerpresourceserver.dto.response.RoutePathResponse;
import openerp.openerpresourceserver.entity.DeliveryTripPath;
import openerp.openerpresourceserver.repository.DeliveryTripPathRepository;
import openerp.openerpresourceserver.service.route.RouteService;

@Service
@AllArgsConstructor
public class DeliveryTripPathService {

	private final DeliveryTripPathRepository deliveryTripPathRepository;
	private final RouteService routeService;

	public RoutePathResponse getDeliveryTripPaths(String deliveryTripId) {
		List<CoordinateDTO> coordinates = deliveryTripPathRepository.findByDeliveryTripId(deliveryTripId);
		CoordinateRequest request = CoordinateRequest.builder().coordinates(coordinates).build();
		return routeService.getRoute(request);
	}

	public void saveWaypoints(String tripId, List<CoordinateDTO> coordinates) {
		List<DeliveryTripPath> paths = coordinates.stream()
				.map(coord -> DeliveryTripPath.builder().deliveryTripId(tripId).longitude(coord.getLng())
						.latitude(coord.getLat()).createdStamp(LocalDateTime.now()).build())
				.toList();

		deliveryTripPathRepository.saveAll(paths);
	}

	public List<CoordinateDTO> getWaypoints(String deliveryTripId) {
		return deliveryTripPathRepository.findByDeliveryTripId(deliveryTripId);
	}

}
