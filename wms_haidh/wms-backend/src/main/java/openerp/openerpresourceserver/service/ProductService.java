package openerp.openerpresourceserver.service;

import java.util.List;
import java.util.UUID;

import openerp.openerpresourceserver.entity.Product;
import openerp.openerpresourceserver.entity.ProductInfoProjection;

public interface ProductService {

    List<ProductInfoProjection> getAllProductGeneral();

	Product createProduct(Product product);
	
	boolean deleteProductById(UUID id);

//    boolean deleteProducts(List<String> productIds);
//
//    ProductDetailResponse getById(String id);
//
//    boolean createProductPrice(ProductPriceRequest request);
//
//    List<ProductPriceResponse> getAllProductPrices();
//
//    boolean deleteProductPriceById(String[] ids);
//
//    BigDecimal getCurrPriceByProductId(UUID id);
//
//    Map<UUID, String> getProductNameMap();
}

