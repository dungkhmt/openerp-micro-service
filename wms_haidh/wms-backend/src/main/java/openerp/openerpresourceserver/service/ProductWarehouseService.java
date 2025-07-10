package openerp.openerpresourceserver.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.repository.ProductWarehouseRepository;

@Service
@RequiredArgsConstructor
public class ProductWarehouseService {

    private final ProductWarehouseRepository repository;

    public boolean isProductAvailable(UUID productId, double requestedQuantity) {
        double totalAvailable = repository.getTotalQuantityByProductId(productId);
        return totalAvailable >= requestedQuantity;
    }
}

