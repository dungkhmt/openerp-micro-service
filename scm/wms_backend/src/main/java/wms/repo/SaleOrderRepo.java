package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.PurchaseOrder;
import wms.entity.SaleOrder;

import java.util.List;

public interface SaleOrderRepo extends JpaRepository<SaleOrder, Long> {
    @Query(value = "select so.* from scm_sale_order so" +
            "         where so.status != 'DELETED' and (so.status = :orderStatus or :orderStatus = '')\n", nativeQuery = true)
    Page<SaleOrder> search(Pageable pageable, String orderStatus);
    SaleOrder getOrderById(long id);
    SaleOrder getOrderByCode(String code);
    @Query(value = "SELECT *\n" +
            "FROM scm_sale_order\n" +
            "where EXTRACT(YEAR FROM created_date) = :year and\n" +
            "       EXTRACT(MONTH FROM created_date) = :month\n", nativeQuery = true)
    List<SaleOrder> getOrderByMonth(int month, int year);
}
