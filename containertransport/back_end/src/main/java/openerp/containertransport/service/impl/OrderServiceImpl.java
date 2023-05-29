package openerp.containertransport.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import openerp.containertransport.dto.FacilityResponsiveDTO;
import openerp.containertransport.dto.OrderFilterRequestDTO;
import openerp.containertransport.dto.OrderModel;
import openerp.containertransport.dto.OrdersRes;
import openerp.containertransport.entity.Container;
import openerp.containertransport.entity.Facility;
import openerp.containertransport.entity.Order;
import openerp.containertransport.repo.ContainerRepo;
import openerp.containertransport.repo.FacilityRepo;
import openerp.containertransport.repo.OrderRepo;
import openerp.containertransport.service.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepo orderRepo;
    private final FacilityRepo facilityRepo;
    private final ContainerRepo containerRepo;
    private final ModelMapper modelMapper;
    private final EntityManager entityManager;
    @Override
    public OrderModel createOrder(OrderModel orderModel) {
        Order order = new Order();
        AtomicLong weight = new AtomicLong();
        List<Container> containers = new ArrayList<>();
        if(!orderModel.getContainerIds().isEmpty()){
            orderModel.getContainerIds().forEach((item) -> {
                Container container = containerRepo.findById(item);
                containers.add(container);
                weight.addAndGet(container.getSize());
            });
        }
        if(!containers.isEmpty()) {
            order.setContainers(containers);
        }
        order.setCustomerId(orderModel.getCustomerId());
        Facility facilityFrom = facilityRepo.findById(orderModel.getFromFacilityId()).get();
        Facility facilityTo = facilityRepo.findById(orderModel.getToFacilityId()).get();
        order.setFromFacility(facilityFrom);
        order.setToFacility(facilityTo);
        order.setEarlyDeliveryTime(orderModel.getEarlyDeliveryTime());
        order.setLateDeliveryTime(orderModel.getLateDeliveryTime());
        order.setEarlyPickupTime(orderModel.getEarlyPickupTime());
        order.setLatePickupTime(orderModel.getLatePickupTime());
        order.setType(orderModel.getType());
        order.setWeight(weight.get());
        order.setIsBreakRomooc(orderModel.isBreakRomooc());
        order.setStatus("WAITING PICKUP");
        order.setCreatedAt(System.currentTimeMillis());
        order.setUpdatedAt(System.currentTimeMillis());
        order = orderRepo.save(order);
        order.setOrderCode("ORD" + order.getId());
        order = orderRepo.save(order);

        return convertToModel(order);
    }

    @Override
    public OrdersRes filterOrders(OrderFilterRequestDTO orderFilterRequestDTO) {
        OrdersRes ordersRes = new OrdersRes();

        String sql = "SELECT * FROM container_transport_order WHERE 1=1";
        String sqlCount = "SELECT COUNT(id) FROM container_transport_order WHERE 1=1";
        HashMap<String, Object> params = new HashMap<>();

        if(orderFilterRequestDTO.getOrderCode() != null){
            sql += " AND order_code = :orderCode";
            sqlCount += " AND order_code = :orderCode";
            params.put("orderCode", orderFilterRequestDTO.getOrderCode());
        }
        if(orderFilterRequestDTO.getStatus() != null){
            sql += " AND status = :status";
            sqlCount += " AND status = :status";
            params.put("status", orderFilterRequestDTO.getStatus());
        }
        Query queryCount = this.entityManager.createNativeQuery(sqlCount);
        ordersRes.setCount((Long) queryCount.getSingleResult());
        sql += " ORDER BY updated_at DESC";

        if (orderFilterRequestDTO.getPage() != null && orderFilterRequestDTO.getPageSize() != null) {
            sql += " LIMIT :pageSize OFFSET :index";
            params.put("pageSize", orderFilterRequestDTO.getPageSize());
            params.put("index", orderFilterRequestDTO.getPage() * orderFilterRequestDTO.getPageSize());
            ordersRes.setPage(orderFilterRequestDTO.getPage());
            ordersRes.setPageSize(orderFilterRequestDTO.getPageSize());
        }

        Query query = this.entityManager.createNativeQuery(sql, Order.class);
        for (String i : params.keySet()) {
            query.setParameter(i, params.get(i));
        }
        List<Order> orders = query.getResultList();
        List<OrderModel> orderModels = new ArrayList<>();
        orders.forEach((item) -> orderModels.add(convertToModel(item)));
        ordersRes.setOrderModels(orderModels);
        return ordersRes;
    }

    @Override
    public OrderModel updateOrder(OrderModel orderModel) {
        Order order = orderRepo.findById(orderModel.getId()).get();
        if(orderModel.getStatus() != null) {
            order.setStatus(orderModel.getStatus());
        }
        order = orderRepo.save(order);
        return convertToModel(order);
    }

    public OrderModel convertToModel(Order order){
        OrderModel orderModel = modelMapper.map(order, OrderModel.class);
        FacilityResponsiveDTO fromFacility = buildFacilityResponse(order.getFromFacility());
        FacilityResponsiveDTO toFacility = buildFacilityResponse(order.getToFacility());
        orderModel.setFromFacility(fromFacility);
        orderModel.setToFacility(toFacility);
        return orderModel;
    }
    public FacilityResponsiveDTO buildFacilityResponse(Facility facility) {
        FacilityResponsiveDTO facilityResponsive =  FacilityResponsiveDTO.builder()
                .facilityName(facility.getFacilityName())
                .facilityCode(facility.getFacilityCode())
                .facilityId(facility.getId())
                .longitude(facility.getLongitude())
                .latitude(facility.getLatitude())
                .processingTime(facility.getProcessingTime())
                .address(facility.getAddress())
                .build();
        return facilityResponsive;
    }
}
