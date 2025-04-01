package openerp.openerpresourceserver.service.impl;

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
import openerp.openerpresourceserver.entity.Product;
import openerp.openerpresourceserver.projection.ProductInfoProjection;
import openerp.openerpresourceserver.projection.ProductNameProjection;
import openerp.openerpresourceserver.repository.ProductRepository;
import openerp.openerpresourceserver.service.ProductService;
import openerp.openerpresourceserver.service.mongodb.ImageService;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@NoArgsConstructor
public class ProductServiceImpl implements ProductService {

	private ProductRepository productRepository;
	private ImageService imageService;

	@Override
	public Page<ProductInfoProjection> getAllProductGeneral(String searchTerm, Pageable pageable) {
		return productRepository.findProductInfoWithTotalQuantity(searchTerm, pageable);
	}

	@Override
	public boolean deleteProductById(UUID id) {
		if (productRepository.existsById(id)) {
			productRepository.deleteById(id);
			return true;
		}
		return false;
	}

	@Override
	public Product getProductById(UUID productId) {
		Product product = productRepository.findById(productId).orElse(null);
		if (product != null) {
			// Tạo URL cho ảnh dựa trên imageId
			String baseUrl = "http://localhost:8082"; // URL backend hoặc service hosting ảnh
			product.setImageUrl(baseUrl + "/api/images/" + product.getImageId());
		}
		return product;
	}

	public boolean createProduct(ProductCreateRequest productDto, MultipartFile imageFile) {
		try {
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
			newProduct.setArea(productDto.getArea());
			newProduct.setDescription(productDto.getDescription());
			newProduct.setUom(productDto.getUom());

			if (imageFile != null && !imageFile.isEmpty()) {
				String imageId = imageService.saveImage(imageFile);
				newProduct.setImageId(imageId);
			}
			productRepository.save(newProduct);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public boolean updateProduct(ProductCreateRequest productDto, MultipartFile imageFile) {
		try {
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
			existingProduct.setArea(productDto.getArea());
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

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<ProductNameProjection> searchProductNames(String searchTerm) {
		return productRepository.findProductNamesByName(searchTerm);
	}

}
