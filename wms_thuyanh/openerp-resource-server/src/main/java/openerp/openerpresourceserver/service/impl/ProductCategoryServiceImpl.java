package openerp.openerpresourceserver.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.entity.ProductCategory;
import openerp.openerpresourceserver.repo.ProductCategoryRepository;
import openerp.openerpresourceserver.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private ProductCategoryRepository productCategoryRepository;

    @Override
    public List<ProductCategory> getAll() {
        return productCategoryRepository.findAll();
    }
}