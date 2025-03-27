package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.OrderItem;
import openerp.openerpresourceserver.entity.enumentity.OrderItemStatus;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, UUID> {
    void deleteAllByOrderId(UUID orderId);

    List<OrderItem> findAllByOrderId(UUID id);

    int countAllByOrderId(UUID id);

    @Query("select oi from OrderItem oi join Order o on oi.orderId = o.id where o.originHubId = :hubId and oi.status = :status")
    List<OrderItem> findAllByOriginHubIdAndStatus(@Param("hubId") UUID hubId, @Param("status") OrderItemStatus orderItemStatus);
    @Query("select oi from OrderItem oi where oi.orderItemId in :orderItemIds and oi.status = :orderItemStatus")
    List<OrderItem> findAllByIdAndStatus(@Param("orderItemIds") List<UUID> orderItemIds,@Param("orderItemStatus") OrderItemStatus orderItemStatus);
}
