package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface RouteRepo extends JpaRepository<Route, UUID> {

    Route findByCollectorId(UUID collectorId);
    @Query(value = "SELECT fo.order_id, fo.customer_id, fo.weight, fo.volume, fo.from_date_time, fo.to_date_time, fo.location, fo.status\n" +
            "FROM fmd_order fo\n" +
            "INNER JOIN fmd_route_item fri ON fo.order_id = fri.order_id\n" +
            "WHERE fri.Route_id = ?1",

            nativeQuery = true)
    List<Order> getOrdersByRouteId(UUID routeId);
}
