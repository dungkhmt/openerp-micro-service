package openerp.containertransport.repo;

import openerp.containertransport.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {
    @Query(value = "SELECT * FROM container_transport_order WHERE customer_id = :username AND order_code = :orderCode",
    nativeQuery = true)
    Order findByOrderCode(String orderCode, String username);

    @Query(value = "SELECT * FROM container_transport_order WHERE order_code = :orderCode",
            nativeQuery = true)
    Order findByOrderCode(String orderCode);
}
