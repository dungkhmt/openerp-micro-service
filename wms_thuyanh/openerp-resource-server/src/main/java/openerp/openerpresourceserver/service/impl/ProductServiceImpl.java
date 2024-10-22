package openerp.openerpresourceserver.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.cache.RedisCacheService;
import openerp.openerpresourceserver.entity.Product;
import openerp.openerpresourceserver.entity.ProductPrice;
import openerp.openerpresourceserver.entity.ProductWarehouse;
import openerp.openerpresourceserver.model.request.ProductRequest;
import openerp.openerpresourceserver.model.response.ProductDetailQuantityResponse;
import openerp.openerpresourceserver.model.response.ProductDetailResponse;
import openerp.openerpresourceserver.model.response.ProductGeneralResponse;
import openerp.openerpresourceserver.model.response.ProductNoImg;
import openerp.openerpresourceserver.repo.ProductPriceRepository;
import openerp.openerpresourceserver.repo.ProductRepository;
import openerp.openerpresourceserver.repo.ProductWarehouseRepository;
import openerp.openerpresourceserver.service.ProductService;
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
@Slf4j
public class ProductServiceImpl implements ProductService {

    private ProductPriceRepository productPriceRepository;
    private ProductRepository productRepository;
    private ProductWarehouseRepository productWarehouseRepository;
    private RedisCacheService redisCacheService;

    // API tạo hoặc cập nhật sản phẩm
    @Override
    @Transactional
    public Product createProduct(ProductRequest request) {
        log.info("Start create product " + request);
        Product product;
        String productId;
        boolean isCreateRequest = request.getProductId() == null;
        if (!isCreateRequest) {
            productId = request.getProductId();
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
        Product savedProduct = productRepository.save(product);
        productId = savedProduct.getProductId().toString();
        log.info("Saved new product");

        // update redis
        List<Product> products = redisCacheService.getCachedListObject(RedisCacheService.ALL_PRODUCTS_KEY, Product.class);
        if (products != null && !products.isEmpty()) {
            int index = -1;
            for (int i = 0; i < products.size(); i++) {
                Product lProduct = products.get(i);
                if (lProduct.getProductId().toString().equals(productId)) {
                    index = i;
                }
            }
            if (index != -1) {
                products.remove(index);
            }
            products.add(product);
            redisCacheService.setCachedValueWithExpire(RedisCacheService.ALL_PRODUCTS_KEY, products);
        }
        return product;
    }

    // API lấy tất cả sản phẩm kèm ảnh
    @Override
    @Transactional
    public List<ProductGeneralResponse> getAllProductGeneral() {
        List<Product> products = redisCacheService.getCachedListObject(RedisCacheService.ALL_PRODUCTS_KEY, Product.class);
        if (products == null) {
            products = productRepository.findAll();
            redisCacheService.setCachedValueWithExpire(RedisCacheService.ALL_PRODUCTS_KEY, products);
        }

        return products.stream()
                .map(product -> ProductGeneralResponse.builder()
                        .productId(product.getProductId().toString())
                        .name(product.getName())
                        .code(product.getCode())
                        .retailPrice(getCurrPriceByProductId(product.getProductId()))
                        .imageData(product.getImageData())
                        .build())
                .collect(Collectors.toList());
    }

    // API lấy tất cả sản phẩm không có ảnh
    @Override
    @Transactional
    public List<ProductGeneralResponse> getAllProductGeneralWithoutImage() {
        List<Product> products = redisCacheService.getCachedListObject(RedisCacheService.ALL_PRODUCTS_KEY, Product.class);
        if (products == null) {
            products = productRepository.findAll();
            redisCacheService.setCachedValueWithExpire(RedisCacheService.ALL_PRODUCTS_KEY, products);
        }

        return products.stream()
                .map(product -> ProductGeneralResponse.builder()
                        .productId(product.getProductId().toString())
                        .name(product.getName())
                        .code(product.getCode())
                        .retailPrice(getCurrPriceByProductId(product.getProductId()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductGeneralResponse> getListProductNoImage() {
        List<ProductNoImg> resDTO = productRepository.findListProductWithoutImage();
        List<ProductGeneralResponse> res = new ArrayList<>();
        for(ProductNoImg p : resDTO) {
            res.add(ProductGeneralResponse.builder()
                    .productId(p.getProductId().toString())
                    .name(p.getName())
                    .code(p.getCode())
                    .build());
        }
        return res;
    }

    // Hỗ trợ lấy giá sản phẩm
    @Override
    public BigDecimal getCurrPriceByProductId(UUID productId) {
        Date now = new Date();
        List<ProductPrice> prices = productPriceRepository.findCurrentPriceByProductId(productId, now);
        if (!prices.isEmpty()) {
            return prices.get(0).getPrice();
        }
        return BigDecimal.ZERO;
    }

    // API xóa sản phẩm
    @Override
    @Transactional
    public boolean deleteProducts(List<String> productIds) {
        if (productIds.isEmpty()) {
            return true;
        }

        try {
            for (String productId : productIds) {
                productRepository.deleteById(UUID.fromString(productId));
            }

            // Update cache
            List<Product> products = redisCacheService.getCachedListObject(RedisCacheService.ALL_PRODUCTS_KEY, Product.class);
            if (products != null) {
                products.removeIf(product -> productIds.contains(product.getProductId().toString()));
                redisCacheService.setCachedValueWithExpire(RedisCacheService.ALL_PRODUCTS_KEY, products);
            }
            return true;
        } catch (Exception e) {
            log.error("Error when deleting product ids", e);
            return false;
        }
    }
/*
    @Override
    @Transactional
    public ProductDetailResponse getDetailByProductId(String id) {
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
        List<ProductDetailResponse.ProductWarehouseQuantity> warehouseQuantities = productWarehouses.stream()
                .filter(productWarehouse -> productWarehouse.getQuantityOnHand().compareTo(BigDecimal.ZERO) > 0)
                .map(productWarehouse -> ProductDetailResponse.ProductWarehouseQuantity.
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
*/
}
