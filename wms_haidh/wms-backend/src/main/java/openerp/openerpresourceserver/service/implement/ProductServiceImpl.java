package openerp.openerpresourceserver.service.implement;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.Product;
import openerp.openerpresourceserver.entity.ProductInfoProjection;
import openerp.openerpresourceserver.model.request.ProductDto;
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
	public boolean deleteProductById(UUID id) {
		if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
	}

	@Override
	public Product getProductById(UUID productId) {
		return productRepository.findById(productId).orElse(null);
	}

	@Override
	public boolean createProduct(ProductDto productDto, MultipartFile imageFile) {
        try {
            // Kiểm tra nếu sản phẩm đã tồn tại
            Optional<Product> existingProductOpt = productRepository.findByCode(productDto.getCode());

            if (existingProductOpt.isPresent()) {
                // Nếu sản phẩm đã tồn tại, trả về false (không thể tạo mới)
                return false;
            }

            // Tạo mới sản phẩm
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
                byte[] imageBytes = imageFile.getBytes();
                newProduct.setImageData(imageBytes);
                newProduct.setImageContentType(imageFile.getContentType());
                newProduct.setImageSize(imageFile.getSize());
            }

            // Lưu sản phẩm mới vào cơ sở dữ liệu
            productRepository.save(newProduct);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

	@Override
	 public boolean updateProduct(ProductDto productDto, MultipartFile imageFile) {
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
                byte[] imageBytes = imageFile.getBytes();
                existingProduct.setImageData(imageBytes);
                existingProduct.setImageContentType(imageFile.getContentType());
                existingProduct.setImageSize(imageFile.getSize());
            }

            // Lưu sản phẩm đã cập nhật vào cơ sở dữ liệu
            productRepository.save(existingProduct);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }



}

