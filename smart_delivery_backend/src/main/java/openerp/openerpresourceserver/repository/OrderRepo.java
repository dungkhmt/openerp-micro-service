package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.dto.OrderResponseDto;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepo extends JpaRepository<Order, UUID>, OrderRepositoryCustom {
    @Query("SELECT o FROM Order o JOIN Sender s on o.senderId = s.senderId JOIN FETCH Recipient r on r.recipientId = o.recipientId WHERE o.id = :orderId")
    Optional<Order> findByIdWithSenderAndRecipient(@Param("orderId") UUID orderId);


    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.orderId = :orderId")
    Long countNumberOfItems(@Param("orderId") UUID orderId);


    List<Order> findByOriginHubIdOrderByCreatedAtDesc(UUID hubId);

    List<Order> findAllByOriginHubIdAndStatusAndVehicleId(UUID hubId, OrderStatus orderStatus, UUID vehicleId);

    List<Order> findByRouteVehicleId(UUID routeVehicleId);

    List<Order> findByRouteVehicleIdAndStatus(UUID routeVehicleId, OrderStatus orderStatus);
}
