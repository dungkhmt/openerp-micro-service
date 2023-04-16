package wms.service.product;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wms.dto.product.ProductDTO;
import wms.entity.ProductEntity;
import wms.service.BaseService;

@Service
@Slf4j
public class ProductServiceImpl extends BaseService implements IProductService {

    @Override
    public ProductEntity createProduct(ProductDTO productDTO) {
        return null;
    }
}
