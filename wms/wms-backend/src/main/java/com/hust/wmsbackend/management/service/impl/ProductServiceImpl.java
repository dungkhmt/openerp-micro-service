package com.hust.wmsbackend.management.service.impl;

import com.hust.wmsbackend.management.cache.RedisCacheService;
import com.hust.wmsbackend.management.entity.*;
import com.hust.wmsbackend.management.model.request.ProductPriceRequest;
import com.hust.wmsbackend.management.model.request.ProductRequest;
import com.hust.wmsbackend.management.model.response.ProductDetailQuantityResponse;
import com.hust.wmsbackend.management.model.response.ProductDetailResponse;
import com.hust.wmsbackend.management.model.response.ProductGeneralResponse;
import com.hust.wmsbackend.management.model.response.ProductPriceResponse;
import com.hust.wmsbackend.management.repository.*;
import com.hust.wmsbackend.management.service.ProductService;
import com.hust.wmsbackend.management.service.ProductWarehouseService;
import com.hust.wmsbackend.management.service.WarehouseService;
import com.hust.wmsbackend.management.utils.DateTimeFormat;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTimeComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@NoArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private ProductWarehouseService productWarehouseService;
    private ProductPriceRepository productPriceRepository;
    private ProductV2Repository productRepository;
    private ProductWarehouseRepository productWarehouseRepository;
    private BayRepository bayRepository;
    private InventoryItemRepository inventoryItemRepository;
    private RedisCacheService redisCacheService;

    private WarehouseService warehouseService;

    @Override
    @Transactional
    public Product createProduct(ProductRequest request) {
        log.info("Start create product " + request);
        Product product;
        boolean isCreateRequest = request.getProductId() == null;
        if (!isCreateRequest) {
            String productId = request.getProductId();
            log.info("Start update product with id " + productId);
            Optional<Product> productOpt = productRepository.findById(UUID.fromString(productId));
            if (productOpt.isPresent()) {
                product = productOpt.get();
            } else {
                log.warn("Not found product with id " + productId);
                return null;
            }
        } else {
            product = Product.builder()
                               .productId(UUID.randomUUID())
                               .build();
        }
        product.setName(request.getName());
        product.setCode(request.getCode());
        product.setDescription(request.getDescription());
        product.setHeight(request.getHeight());
        product.setWeight(request.getWeight());
        product.setArea(request.getArea());
        product.setUom(request.getUom());
        product.setCategoryId(request.getCategoryId() == null
                        ? null
                        : UUID.fromString(request.getCategoryId()));
        if (request.getImage() != null) {
            try {
                MultipartFile image = request.getImage();
                product.setImageData(image.getBytes());
                product.setImageContentType(image.getContentType());
                product.setImageSize(image.getSize());
            } catch (IOException ioe) {
                log.error("Error when get image");
                return null;
            }
        }
        productRepository.save(product);
        log.info("Saved new product");

        // update init product quantity of created product is not allowed
        if (!isCreateRequest) {
            return product;
        }

        // update redis
        List<Product> products = redisCacheService.getCachedListObject(RedisCacheService.ALL_PRODUCTS_KEY, Product.class);
        if (products != null && !products.isEmpty()) {
            products.add(product);
            redisCacheService.setCachedValueWithExpire(RedisCacheService.ALL_PRODUCTS_KEY, products);
        }

        List<ProductRequest.InitProductQuantity> quantityList = request.getInitProductQuantityList();
        Map<String, BigDecimal> normQuantityMap = new HashMap<>();
        if (quantityList != null && !quantityList.isEmpty()) {
            log.info("Init product quantity list is NOT empty");
            Map<String, String> bayIdWarehouseIdMap = new HashMap<>();

            // normalize list by bay id
            for (ProductRequest.InitProductQuantity quantity : quantityList) {
                String bayId = quantity.getBayId();
                bayIdWarehouseIdMap.put(quantity.getBayId(), quantity.getWarehouseId());
                if (!normQuantityMap.containsKey(quantity.getBayId())) {
                    normQuantityMap.put(bayId, quantity.getQuantity());
                } else {
                    BigDecimal newValue = normQuantityMap.get(bayId).add(quantity.getQuantity());
                    normQuantityMap.put(bayId, newValue);
                }
            }

            for (ProductRequest.InitProductQuantity quantity : quantityList) {
                InventoryItem item = InventoryItem.builder()
                    .inventoryItemId(UUID.randomUUID())
                    .bayId(UUID.fromString(quantity.getBayId()))
                    .quantityOnHandTotal(quantity.getQuantity())
                    .importPrice(quantity.getImportPrice())
                    .productId(product.getProductId())
                    .lotId(quantity.getLotId())
                    .currencyUomId("VND")
                    .datetimeReceived(new Date())
                    .warehouseId(UUID.fromString(bayIdWarehouseIdMap.get(quantity.getBayId().toString())))
                    .createdStamp(new Date())
                    .lastUpdatedStamp(new Date())
                    .isInitQuantity(true)
                    .build();
                inventoryItemRepository.save(item);
            }
            log.info("Saved product bay entity");

            // update product warehouse quantity
            List<Bay> bays = bayRepository.findAll();
            Map<String, String> bayWarehouseMap = new HashMap<>(); // key = bayId, value = warehouseId -> For fast lookup
            for (Bay bay : bays) {
                bayWarehouseMap.put(bay.getBayId().toString(), bay.getWarehouseId().toString());
            }
            for (Map.Entry<String, BigDecimal> entry : normQuantityMap.entrySet()) {
                if (bayWarehouseMap.containsKey(entry.getKey())) {
                    UUID warehouseId = UUID.fromString(bayWarehouseMap.get(entry.getKey()));
                    BigDecimal addQuantity = entry.getValue();
                    BigDecimal currQuantity = productWarehouseService.
                        getProductQuantityByWarehouseIdAndProductId(warehouseId, product.getProductId());
                    BigDecimal newQuantity = currQuantity.add(addQuantity);

                    Optional<ProductWarehouse> productWarehouseOpt = productWarehouseRepository.
                        findProductWarehouseByWarehouseIdAndProductId(warehouseId, product.getProductId());
                    ProductWarehouse productWarehouse;
                    if (productWarehouseOpt.isPresent()) {
                        productWarehouse = productWarehouseOpt.get();
                        productWarehouse.setQuantityOnHand(newQuantity);
                    } else {
                        productWarehouse = ProductWarehouse.builder()
                                                           .productId(product.getProductId())
                                                           .warehouseId(warehouseId)
                                                           .productWarehouseId(UUID.randomUUID())
                                                           .quantityOnHand(newQuantity)
                                                           .build();
                    }
                    productWarehouseRepository.save(productWarehouse);
                }
            }
            log.info("Saved product warehouse entity");
        }
        return product;
    }

    private List<Product> getAllProductInCacheElseDatabase() {
        List<Product> products = redisCacheService.getCachedListObject(RedisCacheService.ALL_PRODUCTS_KEY, Product.class);
        if (products == null) {
            log.info("Get all products by repository");
            products = productRepository.findAll();
            redisCacheService.setCachedValueWithExpire(RedisCacheService.ALL_PRODUCTS_KEY, products);
        }
        return products;
    }

    @Override
    public List<ProductGeneralResponse> getAllProductGeneral() {
        List<Product> products = getAllProductInCacheElseDatabase();
        Map<String, BigDecimal> productOnHandQuantityMap = new HashMap<>();
        for (Product product : products) {
            String productId = product.getProductId().toString();
            productOnHandQuantityMap.put(productId,
                 productWarehouseRepository.getTotalOnHandQuantityByProductId(UUID.fromString(productId)));
        }
        List<ProductGeneralResponse> response = products.stream()
                                    .map(product -> ProductGeneralResponse.builder()
                                        .productId(product.getProductId().toString())
                                        .name(product.getName())
                                        .code(product.getCode())
                                        .retailPrice(getCurrPriceByProductId(product.getProductId()))
                                        .imageData(product.getImageData())
                                        .imageContentType(product.getImageContentType())
                                        .productCategoryId(product.getCategoryId().toString())
                                        .onHandQuantity(productOnHandQuantityMap.get(product.getProductId().toString()))
                                        .build())
                                    .collect(Collectors.toList());
        return response;
    }

    @Override
    public List<ProductGeneralResponse> getAllProductGeneralWithoutImage() {
        List<Product> products = getAllProductInCacheElseDatabase();
        Map<String, BigDecimal> productOnHandQuantityMap = new HashMap<>();
        for (Product product : products) {
            String productId = product.getProductId().toString();
            productOnHandQuantityMap.put(productId,
                    productWarehouseRepository.getTotalOnHandQuantityByProductId(UUID.fromString(productId)));
        }
        List<ProductGeneralResponse> response = new ArrayList<>();
        for (Product product : products) {
            BigDecimal onhandQuantity = productOnHandQuantityMap.get(product.getProductId().toString());
            response.add(ProductGeneralResponse.builder()
                    .productId(product.getProductId().toString())
                    .name(product.getName())
                    .code(product.getCode())
                    .retailPrice(getCurrPriceByProductId(product.getProductId()))
                    .onHandQuantity(onhandQuantity == null ? BigDecimal.ZERO : onhandQuantity)
                    .build());
        }
        return response;
    }

    @Override
    @Transactional
    public boolean deleteProducts(List<String> productIds) {
        if (productIds.isEmpty()) {
            log.info("Product ids list for deleting is empty");
            return true;
        }

        try {
            for (String productId : productIds) {
                log.info("Start delete product with id " + productId);
                productRepository.deleteById(UUID.fromString(productId));
            }
            // update redis
            List<Product> products = redisCacheService.getCachedListObject(RedisCacheService.ALL_PRODUCTS_KEY, Product.class);
            if (products != null && !products.isEmpty()) {
                products.removeIf(product -> productIds.contains(product.getProductId().toString()));
            }
            redisCacheService.setCachedValueWithExpire(RedisCacheService.ALL_PRODUCTS_KEY, products);
            return true;
        } catch (Exception e) {
            log.info("Error when deleting product ids list");
            return false;
        }
    }

    @Override
    public ProductDetailResponse getById(String id) {
        UUID productId = UUID.fromString(id);
        Optional<Product> productInfo = productRepository.findById(productId);
        if (!productInfo.isPresent()) {
            log.warn(String.format("Product with id %s is not found", id));
            return null;
        }

        List<ProductDetailQuantityResponse> quantityList =
            productRepository.getProductDetailQuantityResponseByProductId(productId);
        List<ProductWarehouse> productWarehouses = productWarehouseRepository.findAllByProductId(productId);
        Map<UUID, String> warehouseNameMap = warehouseService.getWarehouseNameMap();
        List<ProductDetailResponse.ProductWarehouseQuantity> warehouseQuantities = productWarehouses.stream().map(
                productWarehouse -> ProductDetailResponse.ProductWarehouseQuantity.
                        builder().quantity(productWarehouse.getQuantityOnHand())
                        .warehouseName(warehouseNameMap.get(productWarehouse.getWarehouseId()))
                        .warehouseId(productWarehouse.getWarehouseId().toString())
                        .build())
                .collect(Collectors.toList());
        return ProductDetailResponse.builder()
                                    .productInfo(productInfo.get())
                                    .quantityList(quantityList)
                                    .warehouseQuantities(warehouseQuantities)
                                    .build();
    }

    @Override
    public boolean createProductPrice(ProductPriceRequest request) {
        log.info(String.format("Create product price with request %s", request));
        if (!productRepository.findById(UUID.fromString(request.getProductId())).isPresent()) {
            log.warn(String.format("Product id %s is not exist", request.getProductId()));
            return false;
        }

        if (request.getStartDate() == null) {
            log.warn("Start date must not be null");
            return false;
        }

        if (request.getEndDate() != null && request.getStartDate().after(request.getEndDate())) {
            log.warn("Bad request. Start date is after end date");
            return false;
        }

        ProductPrice productPrice = ProductPrice
            .builder()
            .productPriceId(UUID.randomUUID())
            .price(request.getPrice())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .description(request.getDescription())
            .productId(UUID.fromString(request.getProductId()))
            .build();
        productPriceRepository.save(productPrice);
        log.info("Saved new product price");
        return true;
    }

    @Override
    public List<ProductPriceResponse> getAllProductPrices() {
        List<ProductPriceResponse> response = new ArrayList<>();
        List<Product> products = getAllProductInCacheElseDatabase();
        for (Product product : products) {
            List<ProductPrice> prices = productPriceRepository.findAllByProductId(product.getProductId());
            BigDecimal currPrice = getCurrPriceByProductId(product.getProductId());
            List<ProductPriceResponse.ProductHistoryPrices> historyPrices = prices.stream()
                .map(price -> ProductPriceResponse.ProductHistoryPrices.builder()
                    .price(price.getPrice())
                    .startDate(DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY, price.getStartDate()))
                    .endDate(DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY, price.getEndDate()))
                    .description(price.getDescription())
                    .productPriceId(price.getProductPriceId())
                    .build())
                .collect(Collectors.toList());
            response.add(ProductPriceResponse
                             .builder()
                             .currPrice(currPrice)
                             .productName(product.getName())
                             .productId(product.getProductId())
                             .historyPrices(historyPrices)
                             .build());
        }
        return response;
    }

    @Override
    @Transactional
    public boolean deleteProductPriceById(String[] ids) {
        try {
            for (String id : ids) {
                productPriceRepository.deleteById(UUID.fromString(id));
            }
            return true;
        } catch (Exception e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public BigDecimal getCurrPriceByProductId(UUID productId) {
        List<ProductPrice> prices = productPriceRepository.findAllByProductId(productId);
        Date now = new Date();
        BigDecimal currPrice = null;
        DateTimeComparator comparator = DateTimeComparator.getDateOnlyInstance();
        for (ProductPrice price : prices) {
//            if (price.getStartDate().before(now) && (price.getEndDate() == null || price.getEndDate().after(now))) {
//                currPrice = price.getPrice();
//                break;
//            }
            boolean isStartDateBefore = comparator.compare(price.getStartDate(), now) <= 0;
            boolean isEndDateAfter = price.getEndDate() == null || comparator.compare(price.getEndDate(), now) >= 0;
            if (isStartDateBefore && isEndDateAfter) {
                currPrice = price.getPrice();
                break;
            }
        }
        return currPrice;
    }

    @Override
    public Map<UUID, String> getProductNameMap() {
        List<Product> products = getAllProductInCacheElseDatabase();
        Map<UUID, String> map = new HashMap<>();
        for (Product product : products) {
            map.put(product.getProductId(), product.getName());
        }
        return map;
    }
}
