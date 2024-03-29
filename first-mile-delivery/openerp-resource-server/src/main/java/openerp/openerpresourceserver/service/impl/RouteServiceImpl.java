package openerp.openerpresourceserver.service.impl;

import openerp.openerpresourceserver.dto.RouteDTO;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.repo.CollectorRepo;
import openerp.openerpresourceserver.repo.OrderRepo;
import openerp.openerpresourceserver.repo.RouteItemRepo;
import openerp.openerpresourceserver.repo.RouteRepo;
import openerp.openerpresourceserver.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RouteServiceImpl implements RouteService {

    @Autowired
    RouteRepo routeRepo;

    @Autowired
    CollectorRepo collectorRepo;

    @Autowired
    OrderRepo orderRepo;

    @Autowired
    RouteItemRepo routeItemRepo;

    @Override
    public void addNewOrderToRoute(UUID routeId, UUID orderId) {

        routeItemRepo.saveWithSequence(routeId, orderId);
        orderRepo.updateStatus(orderId, Order.IN_PROGRESS);


    }

    @Override
    public void removeOrderFromRoute(UUID routeId, UUID orderId) {

            routeItemRepo.deleteAndUpdateSequence(routeId, orderId);
            orderRepo.updateStatus(orderId, Order.PENDING);
    }

    @Override
    public Route createRoute(RouteDTO routeDTO) {

        Collector collector = collectorRepo.findById(routeDTO.getCollectorId()).orElse(null);
        if(collector == null){
            return null;
        }
        Route route = Route.builder()
                .collector(collector)
                .build();
        return routeRepo.save(route);
    }

    @Override
    public Route updateRoute(UUID id, RouteDTO routeDTO) {
        Collector collector = collectorRepo.findById(routeDTO.getCollectorId()).orElse(null);
        if(collector == null){
            return null;
        }
        Route route = routeRepo.findById(id).orElse(null);
        if(route == null){
            return null;
        }
        route.setCollector(collector);
        return routeRepo.save(route);

    }

    @Override
    public boolean deleteRoute(UUID id) {
        try {
            routeRepo.deleteById(id);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public Route getRoute(UUID id) {
        return routeRepo.findById(id).orElse(null);
    }



    @Override
    public List<Order> getAllOrdersInRoute(UUID routeId) {



        return routeRepo.getOrdersByRouteId(routeId);
    }
}
