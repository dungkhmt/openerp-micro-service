package openerp.containertransport.repo;

import openerp.containertransport.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {
    @Query(value = "SELECT * FROM container_transport_order WHERE customer_id = :username AND uid = :uid",
    nativeQuery = true)
    Order findByUid(String uid, String username);

    @Query(value = "SELECT * FROM container_transport_order WHERE uid = :uid",
            nativeQuery = true)
    Order findByUid(String uid);
}
