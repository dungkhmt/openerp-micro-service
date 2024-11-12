package openerp.openerpresourceserver.service.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.ProductCategory;
import openerp.openerpresourceserver.repository.ProductCategoryRepository;
import openerp.openerpresourceserver.service.ProductCategoryService;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private ProductCategoryRepository productCategoryRepository;

    @Override
    public List<ProductCategory> getAll() {
        return productCategoryRepository.findAll();
    }
}
