package com.hust.wmsbackend.v2.service.serviceImplement;

import com.hust.wmsbackend.management.entity.Product;
import com.hust.wmsbackend.management.model.request.CartItemRequest;
import com.hust.wmsbackend.v2.model.response.CartItemResponse2;
import com.hust.wmsbackend.management.repository.ProductV2Repository;
import com.hust.wmsbackend.v2.repo.ProductWarehouseRepository2;
import com.hust.wmsbackend.v2.service.CartService;
import com.hust.wmsbackend.management.service.DeliveryService;
import com.hust.wmsbackend.v2.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class CartServiceImplVD implements CartService {

    private ProductV2Repository productRepository;

    private ProductService productService;
    private DeliveryService deliveryService;
    private ProductWarehouseRepository2 productWarehouseRepository;

    @Override
    public CartItemResponse2 calculateCartFee(CartItemRequest request) {
        List<CartItemRequest.Item> itemList = request.getItems();

        if (itemList == null || itemList.isEmpty()) {
            log.info("Cart item request is empty....");
            return null;
        }

        List<CartItemResponse2.Item> items = new ArrayList<>();
        BigDecimal itemCost = BigDecimal.ZERO;

        for (CartItemRequest.Item itemRequest : itemList) {
            Optional<Product> productOpt = productRepository.findById(UUID.fromString(itemRequest.getProductId()));

            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                BigDecimal currPrice = productService.getCurrPriceByProductId(product.getProductId());

                if (currPrice == null) {
                    log.warn("Product " + product.getProductId() + " hasn't configured price. Please try another");
                    return null;
                }

                long quantity = itemRequest.getQuantity();
                BigDecimal maxQuantity = productWarehouseRepository.getTotalOnHandQuantityByProductId(product.getProductId());
                items.add(CartItemResponse2.Item.builder()
                        .productId(product.getProductId())
                        .imageData(product.getImageData())
                        .imageContentType(product.getImageContentType())
                        .name(product.getName())
                        .priceUnit(currPrice)
                        .quantity(quantity)
                        .maxQuantity(maxQuantity)
                        .build());

                itemCost = itemCost.add(currPrice.multiply(BigDecimal.valueOf(quantity)));
            }
        }

        BigDecimal deliveryFee = deliveryService.calDeliveryFee(request.getLongitude(), request.getLatitude());
        BigDecimal totalOrderCost = itemCost.add(deliveryFee);

        return CartItemResponse2.builder()
                .items(items)
                .totalProductCost(itemCost)
                .deliveryFee(deliveryFee)
                .totalOrderCost(totalOrderCost)
                .build();
    }

}
