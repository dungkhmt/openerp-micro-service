package openerp.openerpresourceserver.service;

import java.util.List;
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
}

