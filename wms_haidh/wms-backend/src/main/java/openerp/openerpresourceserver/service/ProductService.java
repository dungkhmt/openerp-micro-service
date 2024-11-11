package openerp.openerpresourceserver.service;

import java.util.List;

import openerp.openerpresourceserver.entity.Product;
import openerp.openerpresourceserver.entity.ProductInfoProjection;
import openerp.openerpresourceserver.entity.ProductProjection;
import openerp.openerpresourceserver.model.response.ProductGeneralResponse;

public interface ProductService {

//    Product createProduct(ProductRequest request);
//
    List<ProductInfoProjection> getAllProductGeneral();

    List<ProductGeneralResponse> getAllProductGeneralWithoutImage();

//    boolean deleteProducts(List<String> productIds);
//
//    ProductDetailResponse getById(String id);
//
//    boolean createProductPrice(ProductPriceRequest request);
//
//    List<ProductPriceResponse> getAllProductPrices();
//
//    boolean deleteProductPriceById(String[] ids);
//
//    BigDecimal getCurrPriceByProductId(UUID id);
//
//    Map<UUID, String> getProductNameMap();
}

