package openerp.openerpresourceserver.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.dto.request.SaleOrderItemDTO;
import openerp.openerpresourceserver.dto.response.SaleOrderItemDetailResponse;
import openerp.openerpresourceserver.dto.response.SaleOrderItemResponse;
import openerp.openerpresourceserver.entity.SaleOrderItem;
import openerp.openerpresourceserver.repository.SaleOrderItemRepository;

@Service
public class SaleOrderItemService {
    private final SaleOrderItemRepository saleOrderItemRepository;

    public SaleOrderItemService(SaleOrderItemRepository saleOrderItemRepository) {
        this.saleOrderItemRepository = saleOrderItemRepository;
    }
    
    public List<SaleOrderItemResponse> getSaleOrderItems(UUID id) {
        return saleOrderItemRepository.findSaleOrderItems(id);
    }
    
    public SaleOrderItemDetailResponse getSaleOrderItemDetail(UUID id) {
        return saleOrderItemRepository.findSaleOrderItemDetailById(id);
    }
    
    public void createItems(List<SaleOrderItemDTO> itemDTOs, UUID orderId) {
        List<SaleOrderItem> items = itemDTOs.stream()
                .map(dto -> {
                    SaleOrderItem item = new SaleOrderItem();
                    item.setOrderId(orderId);
                    item.setProductId(dto.getProductId());
                    item.setQuantity(dto.getQuantity());
                    item.setPriceUnit(dto.getPriceUnit());
                    item.setCompleted(0);
                    item.setLastUpdated(LocalDateTime.now());
                    return item;
                }).collect(Collectors.toList());

        saleOrderItemRepository.saveAll(items);
    }
}

