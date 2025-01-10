package openerp.openerpresourceserver.service;


import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import openerp.openerpresourceserver.entity.Product;
import openerp.openerpresourceserver.entity.projection.ProductInfoProjection;
import openerp.openerpresourceserver.entity.projection.ProductNameProjection;
import openerp.openerpresourceserver.model.request.ProductCreate;

public interface ProductService {

    Page<ProductInfoProjection> getAllProductGeneral(String searchTerm,Pageable pageable);
	
	boolean deleteProductById(UUID id);

	Product getProductById(UUID productId);

	boolean updateProduct(ProductCreate productDto, MultipartFile imageFile);

	boolean createProduct(ProductCreate productDto, MultipartFile imageFile);
	
	List<ProductNameProjection> searchProductNames(String searchTerm);

}

