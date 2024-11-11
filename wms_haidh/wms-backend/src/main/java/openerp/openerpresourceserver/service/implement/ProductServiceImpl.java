package openerp.openerpresourceserver.service.implement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.Product;
import openerp.openerpresourceserver.entity.ProductInfoProjection;
import openerp.openerpresourceserver.model.response.ProductGeneralResponse;
import openerp.openerpresourceserver.repository.ProductRepository;
import openerp.openerpresourceserver.service.ProductService;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@NoArgsConstructor
public class ProductServiceImpl implements ProductService {
	
	private ProductRepository productRepository;
//	private ProductWarehouseRepository productWarehouseRepository;

    private List<Product> getAllProductInCacheElseDatabase() {
//        List<Product> products = redisCacheService.getCachedListObject(RedisCacheService.ALL_PRODUCTS_KEY, Product.class);
//        if (products == null) {
//            log.info("Get all products by repository");
    	List<Product> products = productRepository.findAll();
//            redisCacheService.setCachedValueWithExpire(RedisCacheService.ALL_PRODUCTS_KEY, products);
//        }
        return products;
    }

    @Override
    public List<ProductGeneralResponse> getAllProductGeneralWithoutImage() {
        List<Product> products = getAllProductInCacheElseDatabase();
//        Map<String, BigDecimal> productOnHandQuantityMap = new HashMap<>();
//        for (Product product : products) {
//            String productId = product.getProductId().toString();
//            productOnHandQuantityMap.put(productId,
//                    productWarehouseRepository.getTotalOnHandQuantityByProductId(UUID.fromString(productId)));
//        }
        List<ProductGeneralResponse> response = new ArrayList<>();
        for (Product product : products) {
//            BigDecimal onhandQuantity = productOnHandQuantityMap.get(product.getProductId().toString());
            response.add(ProductGeneralResponse.builder()
                    .productId(product.getProductId().toString())
                    .name(product.getName())
                    .code(product.getCode())
                    .retailPrice(getCurrPriceByProductId(product.getProductId()))
//                    .onHandQuantity(onhandQuantity == null ? BigDecimal.ZERO : onhandQuantity)
                    .onHandQuantity(null)
                    .canBeDelete(checkProductCanBeDelete(product.getProductId()))
                    .build());
        }
        return response;
    }
    
    public BigDecimal getCurrPriceByProductId(UUID productId) {
		return null;
    }
    
    private boolean checkProductCanBeDelete(UUID productId) {
        return true;
    }

	@Override
	public List<ProductInfoProjection> getAllProductGeneral() {
		return productRepository.findProductInfoWithTotalQuantity();
	}


}

