package wms.service.product;

import wms.dto.product.ProductDTO;
import wms.entity.ProductEntity;

public interface IProductService {
    ProductEntity createProduct(ProductDTO productDTO);

}
