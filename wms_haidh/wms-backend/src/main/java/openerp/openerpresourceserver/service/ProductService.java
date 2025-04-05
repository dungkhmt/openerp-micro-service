package openerp.openerpresourceserver.service;


import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import openerp.openerpresourceserver.dto.request.ProductCreateRequest;
import openerp.openerpresourceserver.entity.Product;
import openerp.openerpresourceserver.projection.ProductDetailProjection;
import openerp.openerpresourceserver.projection.ProductInfoProjection;
import openerp.openerpresourceserver.projection.ProductNameProjection;
import openerp.openerpresourceserver.projection.ProductProjection;

public interface ProductService {

    Page<ProductInfoProjection> getAllProductGeneral(String searchTerm,Pageable pageable);
	
	boolean deleteProductById(UUID id);

	Product getProductById(UUID productId);

	boolean updateProduct(ProductCreateRequest productDto, MultipartFile imageFile);

	boolean createProduct(ProductCreateRequest productDto, MultipartFile imageFile);
	
	List<ProductNameProjection> searchProductNames(String searchTerm);
	
	public ProductDetailProjection getProductDetail(UUID productId);

	Page<ProductProjection> getProducts(Pageable pageable, String searchTerm, UUID categoryId);

}

