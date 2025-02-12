package openerp.openerpresourceserver.service;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import openerp.openerpresourceserver.entity.Product;
import openerp.openerpresourceserver.entity.ProductInfoProjection;
import openerp.openerpresourceserver.model.request.ProductDto;

public interface ProductService {

    List<ProductInfoProjection> getAllProductGeneral();
	
	boolean deleteProductById(UUID id);

	Product getProductById(UUID productId);

	boolean updateProduct(ProductDto productDto, MultipartFile imageFile);

	boolean createProduct(ProductDto productDto, MultipartFile imageFile);

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

