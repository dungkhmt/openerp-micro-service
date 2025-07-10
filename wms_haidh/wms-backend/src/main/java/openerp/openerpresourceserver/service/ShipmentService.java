package openerp.openerpresourceserver.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.ShipmentCreateRequest;
import openerp.openerpresourceserver.entity.Shipment;
import openerp.openerpresourceserver.repository.ShipmentRepository;

@Service
@RequiredArgsConstructor
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;

    private static final String SHIPMENT_PREFIX = "SP";
    private String generateShipmentId() {
        long count = shipmentRepository.count() + 1;
        return String.format(SHIPMENT_PREFIX + "%05d", count);
    }
    
    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAllUpcomingShipments();
    }
    
    public Page<Shipment> getAllShipments(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "lastUpdatedStamp"));

        if (search == null || search.trim().isEmpty()) {
            return shipmentRepository.findAll(pageable);
        }

        return shipmentRepository.findByShipmentIdContainingIgnoreCase(search, pageable);
    }

    
    public Shipment createShipment(ShipmentCreateRequest request, String userLoginId) {
        Shipment shipment = new Shipment();
        shipment.setShipmentId(generateShipmentId());
        shipment.setExpectedDeliveryStamp(request.getExpectedDeliveryStamp());
        shipment.setCreatedBy(userLoginId);
        shipment.setCreatedStamp(LocalDateTime.now());
        shipment.setLastUpdatedStamp(LocalDateTime.now());
        return shipmentRepository.save(shipment);
    }
}

