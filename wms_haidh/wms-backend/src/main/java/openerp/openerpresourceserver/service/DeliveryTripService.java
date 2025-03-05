package openerp.openerpresourceserver.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.DeliveryTrip;
import openerp.openerpresourceserver.entity.DeliveryTripItem;
import openerp.openerpresourceserver.entity.projection.DeliveryTripGeneralProjection;
import openerp.openerpresourceserver.entity.projection.DeliveryTripProjection;
import openerp.openerpresourceserver.model.request.DeliveryTripRequest;
import openerp.openerpresourceserver.repository.DeliveryTripItemRepository;
import openerp.openerpresourceserver.repository.DeliveryTripRepository;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DeliveryTripService {

	private static final String TRIP_PREFIX = "TRP_";
    private static final String TRIP_ITEM_PREFIX = "TRP_ITEM_";
    
    private DeliveryTripRepository deliveryTripRepository;
    private DeliveryTripItemRepository deliveryTripItemRepository;
    private DeliveryTripPathService deliveryTripPathService;
    private AssignedOrderItemService assignedOrderItemService;
    
    public Page<DeliveryTripProjection> getFilteredDeliveryTrips(String status, Pageable pageable) {
        return deliveryTripRepository.findFilteredDeliveryTrips(status, pageable);
    }
    
    public DeliveryTripGeneralProjection getDeliveryTripById(String deliveryTripId) {
        return deliveryTripRepository.findDeliveryTripById(deliveryTripId)
                .orElseThrow(() -> new EntityNotFoundException("DeliveryTrip not found with id: " + deliveryTripId));
    }
     
    private String generateTripId() {
        long count = deliveryTripRepository.count() + 1;
        return String.format(TRIP_PREFIX + "%05d", count);
    }

    private String generateTripItemId() {
        long count = deliveryTripItemRepository.count() + 1;
        return String.format(TRIP_ITEM_PREFIX + "%05d", count);
    }
    
    public DeliveryTrip createDeliveryTrip(DeliveryTripRequest payload) {
        String tripId = generateTripId();
        DeliveryTrip trip = DeliveryTrip.builder()
            .deliveryTripId(tripId)
            .warehouseId(payload.getWarehouseId())
            .deliveryPersonId(payload.getDeliveryPersonId())
            .description(payload.getDescription())
            .shipmentId(payload.getShipmentId())
            .totalWeight(payload.getTotalWeight())
            .totalLocations(payload.getTotalLocations())
            .distance(payload.getDistance())
            .createdBy(payload.getAssignedBy())
            .createdStamp(LocalDateTime.now())
            .lastUpdatedStamp(LocalDateTime.now())
            .isDeleted(false)
            .status("CREATED")
            .build();

        deliveryTripRepository.save(trip);

        for (DeliveryTripItem item : payload.getItems()) {
            item.setDeliveryTripItemId(generateTripItemId());
            item.setDeliveryTripId(tripId);
            item.setCreatedStamp(LocalDateTime.now());
            item.setLastUpdatedStamp(LocalDateTime.now());
            item.setDeleted(false);
            item.setStatus("CREATED");
            deliveryTripItemRepository.save(item);
            assignedOrderItemService.updateAssignedOrderItemStatus(item.getAssignedOrderItemId());
            
        }

        deliveryTripPathService.saveRoutePath(tripId, payload.getCoordinates());

        return trip;
    }
    
    @Transactional
    public boolean cancelDeliveryTrip(String deliveryTripId) {
        Optional<DeliveryTrip> optionalTrip = deliveryTripRepository.findById(deliveryTripId);
        
        if (optionalTrip.isPresent()) {
            DeliveryTrip trip = optionalTrip.get();
            
            if ("CREATED".equals(trip.getStatus())) {
                trip.setStatus("CANCELLED");
                deliveryTripRepository.save(trip);
                return true;
            }
        }
        
        return false;
    }

    
       
    
}
