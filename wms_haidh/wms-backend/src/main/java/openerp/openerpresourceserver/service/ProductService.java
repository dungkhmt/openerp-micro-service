package openerp.openerpresourceserver.service;


import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import openerp.openerpresourceserver.entity.Product;
import openerp.openerpresourceserver.entity.ProductInfoProjection;
import openerp.openerpresourceserver.model.request.ProductDto;

public interface ProductService {

    Page<ProductInfoProjection> getAllProductGeneral(String searchTerm,Pageable pageable);
	
	boolean deleteProductById(UUID id);

	Product getProductById(UUID productId);

	boolean updateProduct(ProductDto productDto, MultipartFile imageFile);

	boolean createProduct(ProductDto productDto, MultipartFile imageFile);

}

