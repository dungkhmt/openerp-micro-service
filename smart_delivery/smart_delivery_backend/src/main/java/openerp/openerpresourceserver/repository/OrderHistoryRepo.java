package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.entity.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderHistoryRepo extends JpaRepository<OrderHistory, UUID> {
    List<OrderHistory> findByOrderIdOrderByCreatedAtDesc(UUID orderId);
} 