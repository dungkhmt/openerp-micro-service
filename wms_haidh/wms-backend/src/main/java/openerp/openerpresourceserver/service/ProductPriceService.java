package openerp.openerpresourceserver.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.dto.request.ProductPriceCreateRequest;
import openerp.openerpresourceserver.entity.ProductPrice;
import openerp.openerpresourceserver.repository.ProductPriceRepository;

@Service
public class ProductPriceService {

    @Autowired
    private ProductPriceRepository productPriceRepository;

    public Page<ProductPrice> getProductPricesByProductId(UUID productId, Pageable pageable) {
        return productPriceRepository.findByProductId(productId, pageable);
    }
    
    public ProductPrice createProductPrice(ProductPriceCreateRequest request) {
        ProductPrice productPrice = ProductPrice.builder()
                .productId(request.getProductId())
                .price(request.getPrice())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        return productPriceRepository.save(productPrice);
    }
}

