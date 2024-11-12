package openerp.openerpresourceserver.service.implement;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.Product;
import openerp.openerpresourceserver.entity.ProductInfoProjection;
import openerp.openerpresourceserver.repository.ProductRepository;
import openerp.openerpresourceserver.service.ProductService;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@NoArgsConstructor
public class ProductServiceImpl implements ProductService {
	
	private ProductRepository productRepository;

//    private List<Product> getAllProductInCacheElseDatabase() {
//        List<Product> products = redisCacheService.getCachedListObject(RedisCacheService.ALL_PRODUCTS_KEY, Product.class);
//        if (products == null) {
//            log.info("Get all products by repository");
//    	List<Product> products = productRepository.findAll();
//            redisCacheService.setCachedValueWithExpire(RedisCacheService.ALL_PRODUCTS_KEY, products);
//        }
//        return products;
//    }
    
//    public BigDecimal getCurrPriceByProductId(UUID productId) {
//		return null;
//    }
//    
//    private boolean checkProductCanBeDelete(UUID productId) {
//        return true;
//    }

	@Override
	public List<ProductInfoProjection> getAllProductGeneral() {
		return productRepository.findProductInfoWithTotalQuantity();
	}

	@Override
	public Product createProduct(Product product) {
		 return productRepository.save(product);
	}

	@Override
	public boolean deleteProductById(UUID id) {
		if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
	}


}

