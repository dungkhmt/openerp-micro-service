package openerp.openerpresourceserver.repo;

import jakarta.transaction.Transactional;
import openerp.openerpresourceserver.entity.RouteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface RouteItemRepo extends JpaRepository<RouteItem, Long> {



    RouteItem findByOrderId(UUID orderId);

    RouteItem findByRouteId(UUID routeId);

    RouteItem findByOrderIdAndRouteId(UUID orderId, UUID routeId);

    void deleteByOrderId(UUID orderId);

    void deleteByRouteId(UUID routeId);

    void deleteByOrderIdAndRouteId(UUID orderId, UUID routeId);


    @Transactional
    @Modifying
    @Query(value = "INSERT INTO fmd_route_item (route_id, order_id, sequence) " +
            "SELECT :routeId, :orderId, COALESCE(MAX(sequence), 0) + 1 " +
            "FROM fmd_route_item WHERE route_id = :routeId",
            nativeQuery = true)
    void saveWithSequence(@Param("routeId") UUID routeId, @Param("orderId") UUID orderId);

    @Transactional
    @Modifying
    @Query(value = "WITH Deleted AS (" +
            "  DELETE FROM fmd_route_item " +
            "  WHERE route_id = :routeId AND order_id = :orderId " +
            "  RETURNING sequence" +
            ") " +
            "UPDATE fmd_route_item " +
            "SET sequence = sequence - 1 " +
            "WHERE route_id = :routeId AND sequence > (SELECT sequence FROM Deleted)",
            nativeQuery = true)
    void deleteAndUpdateSequence(@Param("routeId") UUID routeId, @Param("orderId") UUID orderId);

}
