package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Product;
import openerp.openerpresourceserver.model.response.ProductDetailQuantityResponse;
import openerp.openerpresourceserver.model.response.ProductNoImg;
import openerp.openerpresourceserver.model.response.ProductReportResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("select new openerp.openerpresourceserver.model.response.ProductDetailQuantityResponse " +
            "(w.warehouseId, w.name, b.bayId, b.code, ii.quantityOnHandTotal, ii.importPrice, ii.lotId) " +
            "from InventoryItem ii " +
            "join Bay b on b.bayId = ii.bayId " +
            "join Warehouse w on w.warehouseId = b.warehouseId " +
            "where ii.productId = :productId and ii.isInitQuantity = true ")
    List<ProductDetailQuantityResponse> getProductDetailQuantityResponseByProductId(UUID productId);

    @Query("select new openerp.openerpresourceserver.model.response.ProductReportResponse" +
            "(wp.productId , wp.name , " +
            "sum(wii.importPrice * wii.quantityOnHandTotal) , " +
            "sum(wii.quantityOnHandTotal) ) " +
            "from Product wp " +
            "join InventoryItem wii on wp.productId = wii.productId and wii.quantityOnHandTotal > 0 " +
            "group by wp.productId ")
    List<ProductReportResponse> getProductsDataForReport();

    @Query("SELECT p.productId, p.name FROM Product p")
    List<Object[]> findProductIdAndName();

    @Query("select new openerp.openerpresourceserver.model.response.ProductNoImg" +
            "(p.productId, p.name, p.code, p.categoryId) " +
            "from Product p ")
    List<ProductNoImg> findListProductWithoutImage();
}

