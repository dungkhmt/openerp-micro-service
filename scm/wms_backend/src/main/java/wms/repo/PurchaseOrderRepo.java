package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.PurchaseOrder;

import java.util.List;

public interface PurchaseOrderRepo extends JpaRepository<PurchaseOrder, Long>  {
    @Query(value = "select spo.*, sf.name\n" +
            "from scm_purchase_order spo\n" +
            "         left join scm_facility sf on spo.bought_by = sf.code\n" +
            "where spo.is_deleted = 0\n" +
//            "  and spo.status != 'DELETED'\n" +
            "  and (sf.name ilike concat('%', :facilityName, '%'))\n" +
            "  and (spo.status = :orderStatus or :orderStatus = '')\n" +
            "  and (spo.supplier_code ilike concat('%', :supplierCode, '%'))\n" +
            "  and (spo.created_by = :createdBy or :createdBy = '')\n" +
            "  and (sf.name ilike concat('%', :text, '%')\n" +
            "    or spo.supplier_code ilike concat('%', :text, '%')\n" +
            "    or spo.created_by ilike concat('%', :text, '%')\n" +
            "    )", nativeQuery = true)
    Page<PurchaseOrder> search(Pageable pageable, String orderStatus, String facilityName, String createdBy, String supplierCode, String text);
    PurchaseOrder getOrderById(long id);
    @Query(value = "select spo.* from scm_purchase_order spo\n" +
            "         left join scm_purchase_order_item spoi on spo.code = spoi.order_code\n" +
            "         where code = :code and spoi.is_deleted = 0", nativeQuery = true)
    PurchaseOrder getOrderByCode(String code);

    @Query(value = "SELECT *\n" +
            "FROM scm_purchase_order\n" +
            "where EXTRACT(YEAR FROM created_date) = :year and\n" +
            "       EXTRACT(MONTH FROM created_date) = :month\n", nativeQuery = true)
    List<PurchaseOrder> getOrderByMonth(int month, int year);
}
