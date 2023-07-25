package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.PurchaseOrder;
import wms.entity.SaleOrder;

import java.util.List;

public interface SaleOrderRepo extends JpaRepository<SaleOrder, Long> {
    @Query(value = "select so.*, sf.name\n" +
            "            from scm_sale_order so\n" +
            "                     left join scm_customer sf on so.customer_code = sf.code\n" +
            "            where so.is_deleted = 0\n" +
//            "              and so.status != 'DELETED'\n" +
            "              and (sf.name ilike concat('%', :customerName, '%'))\n" +
            "              and (so.status = :orderStatus or :orderStatus = '')\n" +
            "              and (so.created_by = :createdBy or :createdBy = '')\n" +
            "              and (sf.name ilike concat('%', :text, '%')\n" +
            "                or so.status ilike concat('%', :text, '%')\n" +
            "                or so.created_by ilike concat('%', :text, '%')\n" +
            "                )", nativeQuery = true)
    Page<SaleOrder> search(Pageable pageable, String orderStatus, String createdBy, String customerName, String text);
    SaleOrder getOrderById(long id);
    SaleOrder getOrderByCode(String code);
    @Query(value = "SELECT *\n" +
            "FROM scm_sale_order\n" +
            "where EXTRACT(YEAR FROM created_date) = :year and\n" +
            "       EXTRACT(MONTH FROM created_date) = :month\n", nativeQuery = true)
    List<SaleOrder> getOrderByMonth(int month, int year);
}
