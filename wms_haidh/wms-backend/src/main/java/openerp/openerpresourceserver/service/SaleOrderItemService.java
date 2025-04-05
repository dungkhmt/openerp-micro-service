package openerp.openerpresourceserver.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.dto.request.SaleOrderItemDTO;
import openerp.openerpresourceserver.entity.SaleOrderItem;
import openerp.openerpresourceserver.projection.SaleOrderItemDetailProjection;
import openerp.openerpresourceserver.projection.SaleOrderItemProjection;
import openerp.openerpresourceserver.repository.SaleOrderItemRepository;

@Service
public class SaleOrderItemService {
    private final SaleOrderItemRepository saleOrderItemRepository;

    public SaleOrderItemService(SaleOrderItemRepository saleOrderItemRepository) {
        this.saleOrderItemRepository = saleOrderItemRepository;
    }
    
    public List<SaleOrderItemProjection> getSaleOrderItems(UUID id) {
        return saleOrderItemRepository.findSaleOrderItems(id);
    }
    
    public SaleOrderItemDetailProjection getSaleOrderItemDetail(UUID id) {
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
                    item.setCompleted(BigDecimal.ZERO);
                    item.setLastUpdated(LocalDateTime.now());
                    return item;
                }).collect(Collectors.toList());

        saleOrderItemRepository.saveAll(items);
    }
}

