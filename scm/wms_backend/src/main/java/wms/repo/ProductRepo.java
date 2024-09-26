package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.ProductEntity;

import java.util.List;

public interface ProductRepo extends JpaRepository<ProductEntity, Long> {
    @Query(value = "select * from scm_product sd where sd.is_deleted = 0\n" +
            "            and (sd.name ilike concat('%', :productName, '%'))\n" +
            "            and (sd.status = :status or :status = '')\n" +
            "            and (sd.category_code = :category or :category = '')\n" +
            "            and (sd.unit_code ilike concat('%', :unit, '%'))\n" +
            "            and (sd.name ilike concat('%', :textSearch, '%')\n" +
            "                    or sd.code ilike concat('%', :textSearch, '%')\n" +
            "                    or sd.name ilike concat('%', :textSearch, '%')\n" +
            "                    or sd.sku ilike concat('%', :textSearch, '%'))", nativeQuery = true)
    Page<ProductEntity> search(Pageable pageable, String productName, String status, String category, String unit, String textSearch);

    ProductEntity getProductById(long id);
    ProductEntity getProductByCode(String code);
    ProductEntity getProductBySku(String sku);

    @Query(value = "select * from scm_product where is_deleted = 0", nativeQuery = true)
    List<ProductEntity> getAllProduct();
}
