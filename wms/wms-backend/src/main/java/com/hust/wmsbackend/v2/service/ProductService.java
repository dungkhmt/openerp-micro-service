package com.hust.wmsbackend.v2.service;

import com.hust.wmsbackend.management.entity.Product;
import com.hust.wmsbackend.management.model.request.ProductPriceRequest;
import com.hust.wmsbackend.management.model.request.ProductRequest;
import com.hust.wmsbackend.management.model.response.ProductDetailResponse;
import com.hust.wmsbackend.management.model.response.ProductGeneralResponse;
import com.hust.wmsbackend.management.model.response.ProductPriceResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProductService {

        Product createProduct(ProductRequest request);

        List<ProductGeneralResponse> getAllProductGeneral();

        List<ProductGeneralResponse> getAllProductGeneralWithoutImage();

        List<ProductGeneralResponse> getAllProductForSale();

        boolean deleteProducts(List<String> productIds);

        ProductDetailResponse getDetailByProductId(String id);

        boolean createProductPrice(ProductPriceRequest request);

        List<ProductPriceResponse> getAllProductPrices();

        boolean deleteProductPriceById(String[] ids);

        BigDecimal getCurrPriceByProductId(UUID id);

        Map<UUID, String> getProductNameMap();

        Map<UUID, String> getProductNameMapNotInCache();

}
