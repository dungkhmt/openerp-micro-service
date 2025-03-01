package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, UUID> {
    void deleteAllByOrderId(UUID orderId);

    List<OrderItem> findAllByOrderId(UUID id);
}
