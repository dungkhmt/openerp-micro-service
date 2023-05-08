package com.hust.wmsbackend.management.service;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProductWarehouseService {

    BigDecimal getProductQuantityByWarehouseIdAndProductId(UUID warehouseId, UUID productId);

}
