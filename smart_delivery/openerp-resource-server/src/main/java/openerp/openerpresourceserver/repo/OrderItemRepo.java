package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, UUID> {
    void deleteAllByOrderId(UUID orderId);
}
