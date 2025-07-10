package openerp.openerpresourceserver.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.dto.request.ProductCreateRequest;
import openerp.openerpresourceserver.dto.response.ProductDetailResponse;
import openerp.openerpresourceserver.dto.response.ProductGeneralResponse;
import openerp.openerpresourceserver.dto.response.ProductInventoryResponse;
import openerp.openerpresourceserver.dto.response.ProductNameResponse;
import openerp.openerpresourceserver.dto.response.ProductPriceResponse;
import openerp.openerpresourceserver.dto.response.ProductResponse;
import openerp.openerpresourceserver.entity.Product;
import openerp.openerpresourceserver.repository.ProductRepository;
import openerp.openerpresourceserver.service.mongodb.ImageService;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@NoArgsConstructor
public class ProductService {

	private ProductRepository productRepository;
	private ImageService imageService;

	public Page<ProductGeneralResponse> getAllProductGeneral(String searchTerm, Pageable pageable) {
		return productRepository.findProductGeneral(searchTerm, pageable);
	}

	public Page<ProductInventoryResponse> getAllProductInventory(String searchTerm, Pageable pageable,
			UUID warehouseId, boolean outOfStockOnly) {
		if (outOfStockOnly) {
			return productRepository.findOutOfStockProductInventory(searchTerm, pageable, warehouseId);
		} else {
			return productRepository.findProductInventory(searchTerm, pageable, warehouseId);
		}
	}

	public Product getProductById(UUID productId) {
		Product product = productRepository.findById(productId).orElse(null);
		if (product != null) {
			// Tạo URL cho ảnh dựa trên imageId
			String baseUrl = "http://localhost:8082"; // URL backend hoặc service hosting ảnh
			product.setImageUrl(baseUrl + "/api/images/" + product.getImageId());
		}
		return product;
	}

	public String getProductNameById(UUID productId) {
		Product product = productRepository.findById(productId).orElse(null);
		if (product != null) {
			return product.getName();
		}
		return null;
	}

	public boolean createProduct(ProductCreateRequest productDto, MultipartFile imageFile) throws IOException {
			Optional<Product> existingProduct = productRepository.findByCode(productDto.getCode());
			if (existingProduct.isPresent()) {
				return false;
			}

			Product newProduct = new Product();
			newProduct.setName(productDto.getName());
			newProduct.setCategoryId(productDto.getCategoryId());
			newProduct.setCode(productDto.getCode());
			newProduct.setWeight(productDto.getWeight());
			newProduct.setHeight(productDto.getHeight());
			newProduct.setDescription(productDto.getDescription());
			newProduct.setUom(productDto.getUom());

			if (imageFile != null && !imageFile.isEmpty()) {
				String imageId = imageService.saveImage(imageFile);
				newProduct.setImageId(imageId);
			}
			productRepository.save(newProduct);
			return true;
	}

	public boolean updateProduct(ProductCreateRequest productDto, MultipartFile imageFile) throws IOException {
			// Tìm sản phẩm theo productId
			Optional<Product> existingProductOpt = productRepository.findById(productDto.getProductId());

			if (existingProductOpt.isEmpty()) {
				// Nếu không tìm thấy sản phẩm, trả về false
				return false;
			}

			// Cập nhật thông tin sản phẩm
			Product existingProduct = existingProductOpt.get();
			existingProduct.setName(productDto.getName());
			existingProduct.setCategoryId(productDto.getCategoryId());
			existingProduct.setCode(productDto.getCode());
			existingProduct.setWeight(productDto.getWeight());
			existingProduct.setHeight(productDto.getHeight());
			existingProduct.setDescription(productDto.getDescription());
			existingProduct.setUom(productDto.getUom());

			if (imageFile != null && !imageFile.isEmpty()) {
				// Xóa ảnh cũ nếu tồn tại
				String oldImageId = existingProduct.getImageId();
				if (oldImageId != null) {
					imageService.deleteImage(oldImageId);
				}

				// Lưu ảnh mới thông qua imageService
				String newImageId = imageService.saveImage(imageFile);
				existingProduct.setImageId(newImageId);

			}

			// Lưu sản phẩm đã cập nhật vào cơ sở dữ liệu
			productRepository.save(existingProduct);
			return true;
	}

	public List<ProductNameResponse> searchProductNames(String searchTerm) {
		return productRepository.findProductNamesByName(searchTerm);
	}

	public Page<ProductResponse> getProducts(Pageable pageable, String searchTerm, UUID categoryId, String sortDir) {
	    String baseUrl = "http://localhost:8082";

	    boolean isDesc = "desc".equalsIgnoreCase(sortDir);

	    if (categoryId != null) {
	        if (isDesc) {
	            return productRepository.findAllProductsByCategoryDesc(pageable, baseUrl, searchTerm, categoryId);
	        } else {
	            return productRepository.findAllProductsByCategoryAsc(pageable, baseUrl, searchTerm, categoryId);
	        }
	    } else {
	        if (isDesc) {
	            return productRepository.findAllProductsWithoutCategoryDesc(pageable, baseUrl, searchTerm);
	        } else {
	            return productRepository.findAllProductsWithoutCategoryAsc(pageable, baseUrl, searchTerm);
	        }
	    }
	}


	public ProductDetailResponse getProductDetail(UUID productId) {
		String baseUrl = "http://localhost:8082";
		return productRepository.findProductDetailById(productId, baseUrl);
	}

	public Page<ProductPriceResponse> getProductsWithPrice(Pageable pageable, String search) {
		return productRepository.findAllWithPrice(pageable, search);
	}

}
