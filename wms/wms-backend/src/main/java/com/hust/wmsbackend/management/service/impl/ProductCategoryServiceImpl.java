package com.hust.wmsbackend.management.service.impl;

import com.hust.wmsbackend.management.entity.ProductCategory;
import com.hust.wmsbackend.management.repository.ProductCategoryRepository;
import com.hust.wmsbackend.management.service.ProductCategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
