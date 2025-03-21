package openerp.openerpresourceserver.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.entity.projection.SaleOrderItemDetailProjection;
import openerp.openerpresourceserver.entity.projection.SaleOrderItemProjection;
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
}

