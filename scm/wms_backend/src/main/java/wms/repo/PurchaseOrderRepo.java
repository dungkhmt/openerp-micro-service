package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.PurchaseOrder;

import java.util.List;

public interface PurchaseOrderRepo extends JpaRepository<PurchaseOrder, Long>  {
    @Query(value = "select po.* from scm_purchase_order po" +
            "         where po.status != 'DELETED' and (po.status = :orderStatus or :orderStatus = '')\n", nativeQuery = true)
    Page<PurchaseOrder> search(Pageable pageable, String orderStatus);
    PurchaseOrder getOrderById(long id);
    PurchaseOrder getOrderByCode(String code);

    @Query(value = "SELECT *\n" +
            "FROM scm_purchase_order\n" +
            "where EXTRACT(YEAR FROM created_date) = :year and\n" +
            "       EXTRACT(MONTH FROM created_date) = :month)\n", nativeQuery = true)
    List<PurchaseOrder> getOrderByMonth(int month, int year);
}
