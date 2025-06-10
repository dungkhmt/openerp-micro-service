package openerp.openerpresourceserver.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.response.CustomerDeliveryResponse;
import openerp.openerpresourceserver.dto.response.DeliveryItemDetailResponse;
import openerp.openerpresourceserver.entity.DeliveryTripItem;
import openerp.openerpresourceserver.repository.DeliveryTripItemRepository;

@Service
@RequiredArgsConstructor
public class DeliveryTripItemService {

	private final DeliveryTripItemRepository deliveryTripItemRepository;

	public List<CustomerDeliveryResponse> getCustomersByDeliveryTripId(String deliveryTripId) {
		return deliveryTripItemRepository.findCustomersByDeliveryTripId(deliveryTripId);
	}

	public Page<DeliveryItemDetailResponse> getDeliveryItems(String deliveryTripId, UUID orderId, int page,
			int size) {
		Pageable pageable = PageRequest.of(page, size);
		return deliveryTripItemRepository.findDeliveryItemsByTripAndOrder(deliveryTripId, orderId, pageable);
	}

	@Transactional
	public void saveItem(DeliveryTripItem item) {
		deliveryTripItemRepository.save(item);
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

	public long countDeliveredItems(UUID orderId) {
		return deliveryTripItemRepository.countDeliveredItemsByOrderId(orderId);
	}

	public List<UUID> findOrderIdsByDeliveryTripId(String deliveryTripId) {
		return deliveryTripItemRepository.findOrderIdsByDeliveryTripId(deliveryTripId);
	}

	public long countUndeliveredItems(String deliveryTripId) {
        return deliveryTripItemRepository.countUndeliveredItems(deliveryTripId);
    }

}
