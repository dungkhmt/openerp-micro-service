package openerp.openerpresourceserver.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.entity.projection.DeliveryTripPathProjection;
import openerp.openerpresourceserver.entity.projection.DeliveryTripProjection;
import openerp.openerpresourceserver.repository.DeliveryTripPathRepository;
import openerp.openerpresourceserver.repository.DeliveryTripRepository;

@Service
public class DeliveryTripService {

    @Autowired
    private DeliveryTripRepository deliveryTripRepository;
    @Autowired
    private DeliveryTripPathRepository deliveryTripPathRepository;

    public Page<DeliveryTripProjection> getFilteredDeliveryTrips(String status, Pageable pageable) {
        return deliveryTripRepository.findFilteredDeliveryTrips(status, pageable);
    }
    
    public List<DeliveryTripPathProjection> getDeliveryTripPaths(String deliveryTripId) {
        return deliveryTripPathRepository.findByDeliveryTripId(deliveryTripId);
    }
    
}
