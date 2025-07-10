package openerp.openerpresourceserver.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import openerp.openerpresourceserver.dto.response.DeliveryTripGeneralResponse;
import openerp.openerpresourceserver.dto.response.DeliveryTripResponse;
import openerp.openerpresourceserver.dto.response.TodayDeliveryTripResponse;
import openerp.openerpresourceserver.entity.DeliveryTrip;
import openerp.openerpresourceserver.entity.DeliveryTripItem;
import openerp.openerpresourceserver.repository.DeliveryTripRepository;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DeliveryTripService {

	private static final String TRIP_PREFIX = "TRP";
	private DeliveryTripRepository deliveryTripRepository;
	private DeliveryTripItemService deliveryTripItemService;
	private DeliveryTripPathService deliveryTripPathService;
	private AssignedOrderItemService assignedOrderItemService;
	private VehicleService vehicleService;
	private DeliveryPersonService deliveryPersonService;
	private OrderService orderService;

	public Page<DeliveryTripResponse> getFilteredDeliveryTrips(String status, Pageable pageable) {
		return deliveryTripRepository.findFilteredDeliveryTrips(status, pageable);
	}

	public Page<TodayDeliveryTripResponse> getTodayDeliveryTrips(String deliveryPersonId, String status,
			Pageable pageable) {
		return deliveryTripRepository.findTodayTripsByDeliveryPerson(deliveryPersonId, status, pageable);
	}

	public DeliveryTripGeneralResponse getDeliveryTripById(String deliveryTripId) {
		return deliveryTripRepository.findDeliveryTripById(deliveryTripId)
				.orElseThrow(() -> new EntityNotFoundException("DeliveryTrip not found with id: " + deliveryTripId));
	}

	private String generateTripId() {
		long count = deliveryTripRepository.count() + 1;
		return String.format(TRIP_PREFIX + "%05d", count);
	}

	@Transactional
	public DeliveryTrip createDeliveryTrip(DeliveryTripCreateRequest payload, String userLoginId) {

		String tripId = generateTripId();
		DeliveryTrip trip = DeliveryTrip.builder().deliveryTripId(tripId).warehouseId(payload.getWarehouseId())
				.vehicleId(payload.getVehicleId()).deliveryPersonId(payload.getDeliveryPersonId())
				.description(payload.getDescription()).shipmentId(payload.getShipmentId())
				.totalWeight(payload.getTotalWeight()).totalLocations(payload.getTotalLocations())
				.distance(payload.getDistance()).createdBy(userLoginId).createdStamp(LocalDateTime.now())
				.lastUpdatedStamp(LocalDateTime.now()).status("CREATED").build();

		deliveryTripRepository.save(trip);

		vehicleService.updateVehicleStatus(payload.getVehicleId(), "BUSY");
		deliveryPersonService.updateDeliveryPersonStatus(payload.getDeliveryPersonId(), "BUSY");

		List<DeliveryTripItem> items = payload.getItems().stream()
				.map(item -> DeliveryTripItem.builder()
						.deliveryTripId(tripId).orderId(item.getOrderId())
						.assignedOrderItemId(item.getAssignedOrderItemId()).sequence(item.getSequence())
						.quantity(item.getQuantity()).status("CREATED")
						.createdStamp(LocalDateTime.now()).lastUpdatedStamp(LocalDateTime.now()).build())
				.toList();

		deliveryTripItemService.saveAllItems(items);

		List<UUID> assignedOrderItemIds = items.stream().map(DeliveryTripItem::getAssignedOrderItemId).toList();
		assignedOrderItemService.updateAssignedOrderItemsStatus(assignedOrderItemIds, "DONE");

		deliveryTripPathService.saveWaypoints(tripId, payload.getCoordinates());

		return trip;
	}

	@Transactional
	public boolean cancelDeliveryTrip(String deliveryTripId) {
		Optional<DeliveryTrip> optionalTrip = deliveryTripRepository.findById(deliveryTripId);

		if (optionalTrip.isPresent()) {
			DeliveryTrip trip = optionalTrip.get();

			if ("CREATED".equals(trip.getStatus())) {

				List<UUID> assignedOrderItemIds = deliveryTripItemService.findIdsByDeliveryTripId(deliveryTripId);

				assignedOrderItemService.updateAssignedOrderItemsStatus(assignedOrderItemIds, "PICKED");

				vehicleService.updateVehicleStatus(trip.getVehicleId(), "AVAILABLE");
				deliveryPersonService.updateDeliveryPersonStatus(trip.getDeliveryPersonId(), "AVAILABLE");

				trip.setStatus("CANCELLED");
				deliveryTripRepository.save(trip);

				return true;
			}
		}
		return false;
	}

	@Transactional
	public boolean startDeliveryTrip(String deliveryTripId) {
		Optional<DeliveryTrip> optionalTrip = deliveryTripRepository.findById(deliveryTripId);

		if (optionalTrip.isPresent()) {
			DeliveryTrip trip = optionalTrip.get();

			if ("CREATED".equals(trip.getStatus())) {

				trip.setStatus("STARTED");
				deliveryTripRepository.save(trip);

				List<UUID> orderIds = deliveryTripItemService.findOrderIdsByDeliveryTripId(deliveryTripId);

				orderService.markOrdersAsDelivering(orderIds);

				return true;
			}
		}
		return false;
	}

	public Page<DeliveryTrip> getDeliveryTripsByShipmentId(String shipmentId, Pageable pageable) {
		return deliveryTripRepository.findByShipmentId(shipmentId, pageable);
	}

	public List<DeliveryTrip> createDeliveryTrip(List<DeliveryTripCreateRequest> payloadList, String userLoginId) {
		List<DeliveryTrip> trips = new ArrayList<>();
		for (DeliveryTripCreateRequest payload : payloadList) {
			DeliveryTrip trip = createDeliveryTrip(payload, userLoginId);
			trips.add(trip);
		}
		return trips;
	}

	@Transactional
	public int markAsDelivered(String deliveryTripId, UUID orderId) {

		DeliveryTrip trip = deliveryTripRepository.findById(deliveryTripId)
				.orElseThrow(() -> new IllegalStateException("DeliveryTrip not found with id: " + deliveryTripId));

		if (!"STARTED".equals(trip.getStatus())) {
			throw new IllegalStateException("Cannot mark items as delivered for this trip.");
		}

		int updatedCount = deliveryTripItemService.markItemsAsDelivered(deliveryTripId, orderId);

		long notYetDeliveredInTrip = deliveryTripItemService.countUndeliveredItems(deliveryTripId);
		if (notYetDeliveredInTrip == 0) {

			trip.setStatus("DONE");
			trip.setLastUpdatedStamp(LocalDateTime.now());

			deliveryTripRepository.save(trip);

			vehicleService.updateVehicleStatus(trip.getVehicleId(), "AVAILABLE");
			deliveryPersonService.updateDeliveryPersonStatus(trip.getDeliveryPersonId(), "AVAILABLE");
		}

		long assigned = assignedOrderItemService.countAssignedItems(orderId);
		long delivered = deliveryTripItemService.countDeliveredItems(orderId);
		if (assigned > 0 && assigned == delivered) {
			orderService.markOrderAsCompleted(orderId);
		}

		return updatedCount;
	}

}
