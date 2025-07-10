package com.hust.wmsbackend.management.service.impl;

import com.hust.wmsbackend.management.entity.ProductWarehouse;
import com.hust.wmsbackend.management.repository.ProductWarehouseRepository;
import com.hust.wmsbackend.management.service.ProductWarehouseService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@NoArgsConstructor
@Slf4j
public class ProductWarehouseServiceImpl implements ProductWarehouseService {

    private ProductWarehouseRepository productWarehouseRepository;

    @Override
    public BigDecimal getProductQuantityByWarehouseIdAndProductId(UUID warehouseId, UUID productId) {
        log.info("Start get product quantity by warehouse id " + warehouseId);
        Optional<ProductWarehouse> productWarehouseOpt =
            productWarehouseRepository.findProductWarehouseByWarehouseIdAndProductId(warehouseId, productId);
        if (productWarehouseOpt.isPresent()) {
            return productWarehouseOpt.get().getQuantityOnHand();
        }
        return new BigDecimal(0);
    }

}
