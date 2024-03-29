package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.RouteDTO;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.Route;

import java.util.List;
import java.util.UUID;

public interface RouteService {

    void addNewOrderToRoute(UUID orderId, UUID routeId);

    void removeOrderFromRoute(UUID orderId, UUID routeId);

    Route createRoute(RouteDTO routeDTO);

    Route updateRoute(UUID id, RouteDTO routeDTO);

    boolean deleteRoute(UUID id);

    Route getRoute(UUID id);



    List<Order> getAllOrdersInRoute(UUID routeId);


}
