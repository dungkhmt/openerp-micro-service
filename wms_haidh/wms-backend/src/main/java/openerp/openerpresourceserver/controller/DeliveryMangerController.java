package openerp.openerpresourceserver.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.DeliveryPerson;
import openerp.openerpresourceserver.entity.DeliveryTrip;
import openerp.openerpresourceserver.entity.Shipment;
import openerp.openerpresourceserver.entity.Warehouse;
import openerp.openerpresourceserver.entity.projection.CustomerOrderProjection;
import openerp.openerpresourceserver.entity.projection.DeliveryOrderItemProjection;
import openerp.openerpresourceserver.entity.projection.DeliveryPersonProjection;
import openerp.openerpresourceserver.entity.projection.DeliveryTripPathProjection;
import openerp.openerpresourceserver.entity.projection.DeliveryTripProjection;
import openerp.openerpresourceserver.model.request.DeliveryTripRequest;
import openerp.openerpresourceserver.service.AssignedOrderItemService;
import openerp.openerpresourceserver.service.DeliveryPersonService;
import openerp.openerpresourceserver.service.DeliveryTripPathService;
import openerp.openerpresourceserver.service.DeliveryTripService;
import openerp.openerpresourceserver.service.OrderService;
import openerp.openerpresourceserver.service.ShipmentService;
import openerp.openerpresourceserver.service.WarehouseService;

@RestController
@RequestMapping("/delivery-manager")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DeliveryMangerController {

	private DeliveryTripService deliveryTripService;
	private DeliveryTripPathService deliveryTripPathService;
	private DeliveryPersonService deliveryPersonService;
	private WarehouseService warehouseService;
	private AssignedOrderItemService assignedOrderItemService;
	private OrderService orderService;
	private ShipmentService shipmentService;

	@GetMapping("/delivery-trips")
	public ResponseEntity<?> getDeliveryTrips(@RequestParam(defaultValue = "CREATED") String status,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size);
			Page<DeliveryTripProjection> deliveryTrips = deliveryTripService.getFilteredDeliveryTrips(status, pageable);
			return ResponseEntity.ok(deliveryTrips);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while fetching delivery trips: " + e.getMessage());
		}
	}

	@GetMapping("/delivery-trips/paths/{deliveryTripId}")
	public ResponseEntity<List<DeliveryTripPathProjection>> getDeliveryTripPaths(@PathVariable String deliveryTripId) {
		List<DeliveryTripPathProjection> paths = deliveryTripPathService.getDeliveryTripPaths(deliveryTripId);
		return ResponseEntity.ok(paths);
	}

	@GetMapping("/warehouse")
	public ResponseEntity<List<Warehouse>> getAllWarehouses() {
		List<Warehouse> warehouses = warehouseService.getAllWarehouses();
		return ResponseEntity.ok(warehouses);
	}

	@GetMapping("/delivery-person")
	public List<DeliveryPersonProjection> getAllDeliveryPersons() {
		return deliveryPersonService.getAllDeliveryPersons();
	}

	@GetMapping("/shipment")
	public List<Shipment> getAllShipments() {
		return shipmentService.getAllShipments();
	}

	@GetMapping("/assignedOrderItems")
	public ResponseEntity<Page<DeliveryOrderItemProjection>> getAllDeliveryOrderItems(@RequestParam UUID warehouseId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<DeliveryOrderItemProjection> orderItems = assignedOrderItemService.getAllDeliveryOrderItems(warehouseId,
				pageable);
		return ResponseEntity.ok(orderItems);
	}

	@GetMapping("/customer-address/{orderId}")
	public ResponseEntity<CustomerOrderProjection> getCustomerOrderById(@PathVariable UUID orderId) {
		Optional<CustomerOrderProjection> order = orderService.getCustomerOrderById(orderId);
		return order.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping("/delivery-trips/create-trip")
	public ResponseEntity<?> createDeliveryTrip(@RequestBody DeliveryTripRequest payload) {
		try {
			DeliveryTrip trip = deliveryTripService.createDeliveryTrip(payload);
			return ResponseEntity.ok(trip);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating trip");
		}
		
	}
	
	@GetMapping("/delivery-persons")
    public ResponseEntity<Page<DeliveryPerson>> getAllDeliveryPersons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(value = "search", required = false) String search) {
        
        Page<DeliveryPerson> deliveryPersons = deliveryPersonService.getAllDeliveryPersons(page, size,search);
        return ResponseEntity.ok(deliveryPersons);
    }
	
	@GetMapping("/shipments")
	public ResponseEntity<Page<Shipment>> getAllShipments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search) {
        
        Page<Shipment> shipments = shipmentService.getAllShipments(page, size, search);
        return ResponseEntity.ok(shipments);
    }

}
