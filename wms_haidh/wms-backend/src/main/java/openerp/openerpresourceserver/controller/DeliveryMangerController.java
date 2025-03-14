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
import openerp.openerpresourceserver.entity.projection.CustomerDeliveryProjection;
import openerp.openerpresourceserver.entity.projection.CustomerOrderProjection;
import openerp.openerpresourceserver.entity.projection.DeliveryItemDetailProjection;
import openerp.openerpresourceserver.entity.projection.DeliveryOrderItemProjection;
import openerp.openerpresourceserver.entity.projection.DeliveryPersonProjection;
import openerp.openerpresourceserver.entity.projection.DeliveryTripGeneralProjection;
import openerp.openerpresourceserver.entity.projection.DeliveryTripPathProjection;
import openerp.openerpresourceserver.entity.projection.DeliveryTripProjection;
import openerp.openerpresourceserver.model.request.DeliveryPersonRequest;
import openerp.openerpresourceserver.model.request.DeliveryTripRequest;
import openerp.openerpresourceserver.model.request.ShipmentRequest;
import openerp.openerpresourceserver.service.AssignedOrderItemService;
import openerp.openerpresourceserver.service.DeliveryPersonService;
import openerp.openerpresourceserver.service.DeliveryTripItemService;
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
	private DeliveryTripItemService deliveryTripItemService;
	private WarehouseService warehouseService;
	private AssignedOrderItemService assignedOrderItemService;
	private OrderService orderService;
	private ShipmentService shipmentService;

	@GetMapping("/shipments")
	public ResponseEntity<Page<Shipment>> getAllShipments(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size, @RequestParam(required = false) String search) {

		Page<Shipment> shipments = shipmentService.getAllShipments(page, size, search);
		return ResponseEntity.ok(shipments);
	}

	@PostMapping("/shipment/create")
    public ResponseEntity<Shipment> createShipment(@RequestBody ShipmentRequest request) {
        Shipment shipment = shipmentService.createShipment(request);
        return ResponseEntity.ok(shipment);
    }
	
	@GetMapping("/shipment/delivery-trips")
    public ResponseEntity<Page<DeliveryTrip>> getDeliveryTrips(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String shipmentId) {
        
        Page<DeliveryTrip> trips = deliveryTripService.getDeliveryTripsByShipmentId(page, size, shipmentId);
        return ResponseEntity.ok(trips);
    }
	
	@GetMapping("/delivery-persons")
	public ResponseEntity<Page<DeliveryPerson>> getAllDeliveryPersons(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size,
			@RequestParam(value = "search", required = false) String search) {

		Page<DeliveryPerson> deliveryPersons = deliveryPersonService.getAllDeliveryPersons(page, size, search);
		return ResponseEntity.ok(deliveryPersons);
	}
	
	@PostMapping("/delivery-persons/create")
    public ResponseEntity<Boolean> createDeliveryPerson(@RequestBody DeliveryPersonRequest request) {
		boolean success = deliveryPersonService.createDeliveryPerson(request);
        return ResponseEntity.ok(success);
    }
	
	@GetMapping("/delivery-persons/{userLoginId}")
    public ResponseEntity<DeliveryPerson> getDeliveryPersonById(@PathVariable String userLoginId) {
        return deliveryPersonService.getDeliveryPersonById(userLoginId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
	
	@PostMapping("/delivery-persons/update")
    public ResponseEntity<Boolean> updateDeliveryPerson(@RequestBody DeliveryPerson updatedPerson) {
        boolean success = deliveryPersonService.updateDeliveryPerson(updatedPerson);
        return ResponseEntity.ok(success);
    }
	
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
	
	@GetMapping("/delivery-trip/general-info/{deliveryTripId}")
	public ResponseEntity<DeliveryTripGeneralProjection> getDeliveryTripDetail(@PathVariable String deliveryTripId) {
		DeliveryTripGeneralProjection tripDetail = deliveryTripService.getDeliveryTripById(deliveryTripId);
		return ResponseEntity.ok(tripDetail);
	}

	@GetMapping("/delivery-trip/customers/{deliveryTripId}")
	public ResponseEntity<List<CustomerDeliveryProjection>> getCustomers(@PathVariable String deliveryTripId) {
		List<CustomerDeliveryProjection> customers = deliveryTripItemService
				.getCustomersByDeliveryTripId(deliveryTripId);
		return ResponseEntity.ok(customers);
	}
	
	@GetMapping("/delivery-trip/paths/{deliveryTripId}")
	public ResponseEntity<List<DeliveryTripPathProjection>> getDeliveryTripPaths(@PathVariable String deliveryTripId) {
		List<DeliveryTripPathProjection> paths = deliveryTripPathService.getDeliveryTripPaths(deliveryTripId);
		return ResponseEntity.ok(paths);
	}

	@GetMapping("/delivery-trip/items")
	public ResponseEntity<Page<DeliveryItemDetailProjection>> getDeliveryItems(@RequestParam String deliveryTripId,
			@RequestParam UUID orderId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Page<DeliveryItemDetailProjection> items = deliveryTripItemService.getDeliveryItems(deliveryTripId, orderId,
				page, size);
		return ResponseEntity.ok(items);
	}
	
	@PostMapping("/delivery-trip/cancel")
    public ResponseEntity<String> cancelDeliveryTrip(@RequestParam String deliveryTripId) {
        boolean cancelled = deliveryTripService.cancelDeliveryTrip(deliveryTripId);
        if (cancelled) {
            return ResponseEntity.ok("Delivery trip cancelled successfully.");
        } else {
            return ResponseEntity.badRequest().body("Delivery trip can only be cancelled if it is in CREATED status.");
        }
    }

}
