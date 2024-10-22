package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.Product;
import openerp.openerpresourceserver.model.request.ProductPriceRequest;
import openerp.openerpresourceserver.model.request.ProductRequest;
import openerp.openerpresourceserver.model.response.ProductDetailResponse;
import openerp.openerpresourceserver.model.response.ProductGeneralResponse;
import openerp.openerpresourceserver.model.response.ProductPriceResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProductService {

    Product createProduct(ProductRequest request);

    List<ProductGeneralResponse> getAllProductGeneral();

    List<ProductGeneralResponse> getAllProductGeneralWithoutImage();

    List<ProductGeneralResponse> getListProductNoImage();

    //List<ProductGeneralResponse> getAllProductForSale();

    boolean deleteProducts(List<String> productIds);

    //ProductDetailResponse getDetailByProductId(String id);

    //boolean createProductPrice(ProductPriceRequest request);

    //List<ProductPriceResponse> getAllProductPrices();

    //boolean deleteProductPriceById(String[] ids);

    BigDecimal getCurrPriceByProductId(UUID id);

    //Map<UUID, String> getProductNameMap();

    //Map<UUID, String> getProductNameMapNotInCache();

}
