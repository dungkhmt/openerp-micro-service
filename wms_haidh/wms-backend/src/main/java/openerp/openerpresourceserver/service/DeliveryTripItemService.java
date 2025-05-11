package openerp.openerpresourceserver.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.entity.DeliveryTripItem;
import openerp.openerpresourceserver.projection.CustomerDeliveryProjection;
import openerp.openerpresourceserver.projection.DeliveryItemDetailProjection;
import openerp.openerpresourceserver.repository.DeliveryTripItemRepository;

@Service
@RequiredArgsConstructor
public class DeliveryTripItemService {

	private static final String TRIP_ITEM_PREFIX = "TRP_ITEM_";
    private final DeliveryTripItemRepository deliveryTripItemRepository;

	public String generateTripItemId() {
		long count = deliveryTripItemRepository.count() + 1;
		return String.format(TRIP_ITEM_PREFIX + "%05d", count);
	}
	
    public List<CustomerDeliveryProjection> getCustomersByDeliveryTripId(String deliveryTripId) {
        return deliveryTripItemRepository.findCustomersByDeliveryTripId(deliveryTripId);
    }
    
    public Page<DeliveryItemDetailProjection> getDeliveryItems(String deliveryTripId, UUID orderId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return deliveryTripItemRepository.findDeliveryItemsByTripAndOrder(deliveryTripId, orderId, pageable);
    }
    
    @Transactional
    public void saveItem(DeliveryTripItem item) {
        deliveryTripItemRepository.save(item);
    }
    
    @Transactional
    public int markItemsAsDeleted(String deliveryTripId) {
        LocalDateTime now = LocalDateTime.now();
        return deliveryTripItemRepository.markItemsAsDeleted(deliveryTripId, now);
    }

    @Transactional
    public void saveAllItems(List<DeliveryTripItem> items) {
        deliveryTripItemRepository.saveAll(items);
    }

	public List<UUID> findIdsByDeliveryTripId(String deliveryTripId) {
		return deliveryTripItemRepository.findIdsByDeliveryTripId(deliveryTripId);
	}
	
	@Transactional
    public int markItemsAsDelivered(String deliveryTripId, UUID orderId) {
        return deliveryTripItemRepository.markItemsAsDelivered(deliveryTripId, orderId);
    }

    public long countUndeliveredItems(String deliveryTripId) {
        return deliveryTripItemRepository.countUndeliveredItems(deliveryTripId);
    }
    
    public long countUndeliveredItemsByOrderId(UUID orderId) {
        return deliveryTripItemRepository.countUndeliveredItemsByOrderId(orderId);
    }


	public List<UUID> findOrderIdsByDeliveryTripId(String deliveryTripId) {
		return deliveryTripItemRepository.findOrderIdsByDeliveryTripId(deliveryTripId);
	}


}

