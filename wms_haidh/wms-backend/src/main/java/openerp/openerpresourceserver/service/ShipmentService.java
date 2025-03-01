package openerp.openerpresourceserver.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.entity.Shipment;
import openerp.openerpresourceserver.repository.ShipmentRepository;

@Service
@RequiredArgsConstructor
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;

    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAllByOrderByExpectedDeliveryStampDesc();
    }
    
    public Page<Shipment> getAllShipments(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);

        if (search != null && !search.trim().isEmpty()) {
            return shipmentRepository.findByShipmentIdContainingIgnoreCase(search, pageable);
        }

        return shipmentRepository.findAll(pageable);
    }
}

