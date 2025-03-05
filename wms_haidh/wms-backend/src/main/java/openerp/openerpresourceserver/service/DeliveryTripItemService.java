package openerp.openerpresourceserver.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.entity.projection.CustomerDeliveryProjection;
import openerp.openerpresourceserver.entity.projection.DeliveryItemDetailProjection;
import openerp.openerpresourceserver.repository.DeliveryTripItemRepository;

@Service
@RequiredArgsConstructor
public class DeliveryTripItemService {

    private final DeliveryTripItemRepository deliveryTripItemRepository;

    public List<CustomerDeliveryProjection> getCustomersByDeliveryTripId(String deliveryTripId) {
        return deliveryTripItemRepository.findCustomersByDeliveryTripId(deliveryTripId);
    }
    
    public Page<DeliveryItemDetailProjection> getDeliveryItems(String deliveryTripId, UUID orderId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return deliveryTripItemRepository.findDeliveryItemsByTripAndOrder(deliveryTripId, orderId, pageable);
    }
}

