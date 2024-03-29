package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepo extends JpaRepository<Order, UUID> {

    List<Order> findAll();

    List<Order> findByCustomerId(UUID customerId);

    Order save(Order order);

    void deleteById(UUID id);

    Optional<Order> findById(UUID id);

    @Query("update Order o set o.status = ?2 where o.id = ?1")
    void updateStatus(UUID id, String status);
}
