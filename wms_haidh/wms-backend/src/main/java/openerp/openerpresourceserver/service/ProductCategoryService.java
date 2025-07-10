package openerp.openerpresourceserver.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.ProductCategory;
import openerp.openerpresourceserver.repository.ProductCategoryRepository;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ProductCategoryService {

    private ProductCategoryRepository productCategoryRepository;

    public List<ProductCategory> getAll() {
        return productCategoryRepository.findAll();
    }
}
