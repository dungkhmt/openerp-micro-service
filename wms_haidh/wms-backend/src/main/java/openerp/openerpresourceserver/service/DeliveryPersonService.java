package openerp.openerpresourceserver.service;

import java.util.List;

import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.entity.projection.DeliveryPersonProjection;
import openerp.openerpresourceserver.repository.DeliveryPersonRepository;

@Service
public class DeliveryPersonService {

    private final DeliveryPersonRepository deliveryPersonRepository;

    public DeliveryPersonService(DeliveryPersonRepository deliveryPersonRepository) {
        this.deliveryPersonRepository = deliveryPersonRepository;
    }

    public List<DeliveryPersonProjection> getAllDeliveryPersons() {
        return deliveryPersonRepository.findAllDeliveryPersons();
    }
}

