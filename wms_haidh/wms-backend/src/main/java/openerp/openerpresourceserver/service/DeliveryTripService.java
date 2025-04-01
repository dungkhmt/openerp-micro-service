package openerp.openerpresourceserver.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.request.DeliveryTripCreateRequest;
import openerp.openerpresourceserver.entity.DeliveryTrip;
import openerp.openerpresourceserver.entity.DeliveryTripItem;
import openerp.openerpresourceserver.projection.DeliveryTripGeneralProjection;
import openerp.openerpresourceserver.projection.DeliveryTripProjection;
import openerp.openerpresourceserver.projection.TodayDeliveryTripProjection;
import openerp.openerpresourceserver.repository.DeliveryTripRepository;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DeliveryTripService {

	private static final String TRIP_PREFIX = "TRP_";
	private DeliveryTripRepository deliveryTripRepository;
	private final DeliveryTripItemService deliveryTripItemService;
	private DeliveryTripPathService deliveryTripPathService;
	private AssignedOrderItemService assignedOrderItemService;

	public Page<DeliveryTripProjection> getFilteredDeliveryTrips(String status, Pageable pageable) {
		return deliveryTripRepository.findFilteredDeliveryTrips(status, pageable);
	}

	public Page<TodayDeliveryTripProjection> getTodayDeliveryTrips(String deliveryPersonId, String status,
			Pageable pageable) {
		return deliveryTripRepository.findTodayTripsByDeliveryPerson(deliveryPersonId, status, pageable);
	}

	public DeliveryTripGeneralProjection getDeliveryTripById(String deliveryTripId) {
		return deliveryTripRepository.findDeliveryTripById(deliveryTripId)
				.orElseThrow(() -> new EntityNotFoundException("DeliveryTrip not found with id: " + deliveryTripId));
	}

	private String generateTripId() {
		long count = deliveryTripRepository.count() + 1;
		return String.format(TRIP_PREFIX + "%05d", count);
	}

	@Transactional
	public DeliveryTrip createDeliveryTrip(DeliveryTripCreateRequest payload) {

		String tripId = generateTripId();
		DeliveryTrip trip = DeliveryTrip.builder().deliveryTripId(tripId).warehouseId(payload.getWarehouseId())
				.deliveryPersonId(payload.getDeliveryPersonId()).description(payload.getDescription())
				.shipmentId(payload.getShipmentId()).totalWeight(payload.getTotalWeight())
				.totalLocations(payload.getTotalLocations()).distance(payload.getDistance())
				.createdBy(payload.getAssignedBy()).createdStamp(LocalDateTime.now())
				.lastUpdatedStamp(LocalDateTime.now()).isDeleted(false).status("CREATED").build();

		deliveryTripRepository.save(trip);

		List<DeliveryTripItem> items = payload.getItems().stream()
				.map(item -> DeliveryTripItem.builder().deliveryTripItemId(deliveryTripItemService.generateTripItemId())
						.deliveryTripId(tripId).orderId(item.getOrderId())
						.assignedOrderItemId(item.getAssignedOrderItemId()).sequence(item.getSequence())
						.quantity(item.getQuantity()).status("CREATED").isDeleted(false)
						.createdStamp(LocalDateTime.now()).lastUpdatedStamp(LocalDateTime.now()).build())
				.toList();

		deliveryTripItemService.saveAllItems(items);

		List<UUID> assignedOrderItemIds = items.stream().map(DeliveryTripItem::getAssignedOrderItemId).toList();
		assignedOrderItemService.updateAssignedOrderItemsStatus(assignedOrderItemIds, "DONE");

		deliveryTripPathService.saveRoutePath(tripId, payload.getCoordinates());

		return trip;
	}

	@Transactional
	public boolean cancelDeliveryTrip(String deliveryTripId) {
		Optional<DeliveryTrip> optionalTrip = deliveryTripRepository.findById(deliveryTripId);

		if (optionalTrip.isPresent()) {
			DeliveryTrip trip = optionalTrip.get();

			if ("CREATED".equals(trip.getStatus())) {

				deliveryTripItemService.markItemsAsDeleted(deliveryTripId);
				List<UUID> assignedOrderItemIds = deliveryTripItemService.findIdsByDeliveryTripId(deliveryTripId);

				assignedOrderItemService.updateAssignedOrderItemsStatus(assignedOrderItemIds, "CREATED");

				trip.setStatus("CANCELLED");
				deliveryTripRepository.save(trip);

				return true;
			}
		}
		return false;
	}

	public Page<DeliveryTrip> getDeliveryTripsByShipmentId(String shipmentId, Pageable pageable) {
		return deliveryTripRepository.findByShipmentId(shipmentId, pageable);
	}

}
