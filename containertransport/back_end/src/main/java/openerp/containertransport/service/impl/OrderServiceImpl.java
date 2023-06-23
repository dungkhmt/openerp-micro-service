package openerp.containertransport.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import openerp.containertransport.dto.*;
import openerp.containertransport.entity.Container;
import openerp.containertransport.entity.Facility;
import openerp.containertransport.entity.Order;
import openerp.containertransport.entity.Relationship;
import openerp.containertransport.repo.ContainerRepo;
import openerp.containertransport.repo.FacilityRepo;
import openerp.containertransport.repo.OrderRepo;
import openerp.containertransport.service.ContainerService;
import openerp.containertransport.service.FacilityService;
import openerp.containertransport.service.OrderService;
import openerp.containertransport.service.RelationshipService;
import openerp.containertransport.utils.RandomUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepo orderRepo;
    private final FacilityRepo facilityRepo;
    private final FacilityService facilityService;
    private final ContainerRepo containerRepo;
    private final ContainerService containerService;
    private final ContainerServiceImpl containerServiceImpl;
    private final RelationshipService relationshipService;
    private final ModelMapper modelMapper;
    private final EntityManager entityManager;
    @Override
    public List<OrderModel> createOrder(OrderModel orderModel) {
        List<OrderModel> orderModels = new ArrayList<>();

        if(orderModel.getType().equals("OE")) {
            Order order = new Order();
            Container container = getContainerNearest(orderModel.getToFacilityId(), orderModel.getWeight());
            Facility fromFacility = facilityRepo.findById(container.getFacility().getId()).get();
            Facility facilityTo = facilityRepo.findById(orderModel.getToFacilityId()).get();
            order.setToFacility(facilityTo);
            order.setFromFacility(fromFacility);
            order.setContainer(container);
            order.setWeight(Long.valueOf(container.getTypeContainer().getSize()));
            order = createAttribute(orderModel, order);
            order = orderRepo.save(order);
            order.setOrderCode("ORD" + order.getId());
            order = orderRepo.save(order);
            orderModels.add(convertToModel(order));
        }
        else {
            if(!orderModel.getContainerIds().isEmpty()){
                orderModel.getContainerIds().forEach((item) -> {
                    Container container = containerRepo.findById(item);
                    Order order = new Order();
                    order.setContainer(container);
                    order.setWeight(Long.valueOf(container.getTypeContainer().getSize()));

                    if(orderModel.getFromFacilityId() != null) {
                        Facility facilityFrom = facilityRepo.findById(orderModel.getFromFacilityId()).get();
                        order.setFromFacility(facilityFrom);
                    }
                    if(orderModel.getToFacilityId() != null) {
                        Facility facilityTo = facilityRepo.findById(orderModel.getToFacilityId()).get();
                        order.setToFacility(facilityTo);
                    }

                    if(orderModel.getType().equals("IE")) {
                        Long facilityId = getFacilityNearest(orderModel.getFromFacilityId());
                        Facility toFacility = facilityRepo.findById(facilityId).get();
                        order.setToFacility(toFacility);
                    }
                    order = createAttribute(orderModel, order);
                    order = orderRepo.save(order);
                    order.setOrderCode("ORD" + order.getId());
                    order = orderRepo.save(order);
                    orderModels.add(convertToModel(order));
                });
            }
        }

        return orderModels;
    }
    public Order createAttribute(OrderModel orderModel, Order order) {
        order.setCustomerId(orderModel.getCustomerId());
        order.setEarlyDeliveryTime(orderModel.getEarlyDeliveryTime());
        order.setLateDeliveryTime(orderModel.getLateDeliveryTime());
        order.setEarlyPickupTime(orderModel.getEarlyPickupTime());
        order.setLatePickupTime(orderModel.getLatePickupTime());
        order.setType(orderModel.getType());
        order.setIsBreakRomooc(orderModel.isBreakRomooc());
        order.setStatus("WAIT_APPROVE");
        order.setUid(RandomUtils.getRandomId());
        order.setCreatedAt(System.currentTimeMillis());
        order.setUpdatedAt(System.currentTimeMillis());
        return order;
    }

    @Override
    public OrdersRes filterOrders(OrderFilterRequestDTO orderFilterRequestDTO) {
        OrdersRes ordersRes = new OrdersRes();

        String sql = "SELECT * FROM container_transport_order WHERE 1=1";
        String sqlCount = "SELECT COUNT(id) FROM container_transport_order WHERE 1=1";
        HashMap<String, Object> params = new HashMap<>();

        if(orderFilterRequestDTO.getOwner() != null) {
            sql += " AND customer_id = :owner";
            sqlCount += " AND customer_id = :owner";
            params.put("owner", orderFilterRequestDTO.getOwner());
        }

        if(orderFilterRequestDTO.getOrderCode() != null){
            sql += " AND order_code = :orderCode";
            sqlCount += " AND order_code = :orderCode";
            params.put("orderCode", orderFilterRequestDTO.getOrderCode());
        }
        if(orderFilterRequestDTO.getStatus() != null && !orderFilterRequestDTO.getStatus().equals("APPROVED")){
            sql += " AND status = :status";
            sqlCount += " AND status = :status";
            params.put("status", orderFilterRequestDTO.getStatus());
        }
        if(orderFilterRequestDTO.getStatus() != null && orderFilterRequestDTO.getStatus().equals("APPROVED")){
            List<String> status = new ArrayList<>();
            status.add("WAIT_APPROVE");
            status.add("DELETED");
            sql += " AND status NOT IN :status";
            sqlCount += " AND status NOT IN :status";
            params.put("status", status);
        }
        Query queryCount = this.entityManager.createNativeQuery(sqlCount);
        for (String i : params.keySet()) {
            queryCount.setParameter(i, params.get(i));
        }
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

    @Override
    public OrderModel getOrderByUid(String uid, String username, boolean isCustomer) {
        Order order = new Order();
        if(isCustomer) {
            order = orderRepo.findByUid(uid, username);
        }
        else {
            order = orderRepo.findByUid(uid);
        }

        return convertToModel(order);
    }

    @Override
    public OrderModel updateOrder(long id, OrderModel orderModel) {
        Order order = orderRepo.findById(id).get();
        if(orderModel.getStatus() != null) {
            order.setStatus(orderModel.getStatus());
        }
        if(orderModel.getEarlyPickupTime() > 0) {
            order.setEarlyPickupTime(orderModel.getEarlyPickupTime());
        }
        if(orderModel.getLatePickupTime() > 0) {
            order.setLatePickupTime(orderModel.getLatePickupTime());
        }
        if(orderModel.getEarlyDeliveryTime() > 0) {
            order.setEarlyDeliveryTime(orderModel.getEarlyDeliveryTime());
        }
        if(orderModel.getLateDeliveryTime() > 0) {
            order.setLateDeliveryTime(orderModel.getLateDeliveryTime());
        }
        order.setUpdatedAt(System.currentTimeMillis());
        order = orderRepo.save(order);
        return convertToModel(order);
    }

    @Override
    public OrderModel updateOrderByUid(String uid, OrderModel orderModel) {
        Order order = orderRepo.findByUid(uid);
        OrderModel orderModelUpdate = updateOrder(order.getId(), orderModel);
        return orderModelUpdate;
    }

    @Override
    public List<OrderModel> updateListOrder(OrderUpdateDTO orderUpdateDTO) {
        List<OrderModel> orderModels = new ArrayList<>();
        String status = orderUpdateDTO.getStatus();
        List<String> uidList = orderUpdateDTO.getUidList();
        uidList.forEach((item) -> {
            Order order = orderRepo.findByUid(item);
            order.setStatus(status);
            orderRepo.save(order);
            orderModels.add(convertToModel(order));
        });
        return orderModels;
    }

    public OrderModel convertToModel(Order order){
        OrderModel orderModel = modelMapper.map(order, OrderModel.class);
        FacilityResponsiveDTO fromFacility = buildFacilityResponse(order.getFromFacility());
        FacilityResponsiveDTO toFacility = buildFacilityResponse(order.getToFacility());
        orderModel.setFromFacility(fromFacility);
        orderModel.setToFacility(toFacility);
        orderModel.setContainerModel(containerServiceImpl.convertToModel(order.getContainer()));
        return orderModel;
    }

    public FacilityResponsiveDTO buildFacilityResponse(Facility facility) {
        FacilityResponsiveDTO facilityResponsive =  FacilityResponsiveDTO.builder()
                .facilityName(facility.getFacilityName())
                .facilityCode(facility.getFacilityCode())
                .facilityId(facility.getId())
                .longitude(facility.getLongitude())
                .latitude(facility.getLatitude())
                .processingTimePickUp(facility.getProcessingTimePickUp())
                .processingTimeDrop(facility.getProcessingTimeDrop())
                .address(facility.getAddress())
                .build();
        return facilityResponsive;
    }

    public Long getFacilityNearest(Long facilityId) {
        AtomicReference<Long> facilityRes = null;

        List<FacilityModel> facilityModels = getAllFacilityAdmin();

        AtomicReference<BigDecimal> distant = new AtomicReference<>(new BigDecimal(Long.MAX_VALUE));
        List<Relationship> relationships = relationshipService.getAllRelationShip();

        facilityModels.forEach((facilityModel) -> {
            relationships.forEach(relationship -> {
                if(relationship.getFromFacility() == facilityId && relationship.getToFacility() == facilityModel.getId()) {
                    if(relationship.getDistant().compareTo(distant.get()) < 0) {
                        distant.set(relationship.getDistant());
                        facilityRes.set(facilityModel.getId());
                    }
                }
            });
        });
        return facilityRes.get();
    }

    public Container getContainerNearest(Long toFacility, long containerSize) {
        List<FacilityModel> facilityModels = getAllFacilityAdmin();

        AtomicReference<BigDecimal> distant = new AtomicReference<>(new BigDecimal(Long.MAX_VALUE));
        List<Relationship> relationships = relationshipService.getAllRelationShip();

        AtomicReference<Long> containerId = null;

        facilityModels.forEach((facilityModel) -> {
            relationships.forEach(relationship -> {
                if(relationship.getFromFacility() == facilityModel.getId()
                        && relationship.getToFacility() == toFacility
                        && checkContainerInDepot(facilityModel.getId(), containerSize) != null
                ) {
                    if(relationship.getDistant().compareTo(distant.get()) < 0) {
                        distant.set(relationship.getDistant());
                        containerId.set(checkContainerInDepot(facilityModel.getId(), containerSize).getId());
                    }
                }
            });
        });
        if(containerId.get() != null) {
            return containerRepo.findById(containerId.get()).get();
        }
        return null;
    }

    public ContainerModel checkContainerInDepot (Long facilityId, long containerSize) {
        ContainerFilterRequestDTO requestDTO = new ContainerFilterRequestDTO();
        requestDTO.setContainerSize(containerSize);
        requestDTO.setFacilityId(facilityId);
        List<ContainerModel> containerModels = containerService.filterContainer(requestDTO).getContainerModels();
        if(containerModels != null && containerModels.size() != 0) {
            return containerModels.get(0);
        }
        return null;
    }

    public List<FacilityModel> getAllFacilityAdmin() {
        FacilityFilterRequestDTO requestDTO = new FacilityFilterRequestDTO();
        requestDTO.setOwner("dungpq");
        requestDTO.setType("Container");
        List<FacilityModel> facilityModels = facilityService.filterFacility(requestDTO).getFacilityModels();
        return facilityModels;
    }
}
