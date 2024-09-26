package openerp.containertransport.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import openerp.containertransport.constants.Constants;
import openerp.containertransport.dto.*;
import openerp.containertransport.dto.dashboard.DashboardTimeOrderDTO;
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
import java.util.Arrays;
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
            if(container == null) {
                return null;
            }
            Facility fromFacility = facilityRepo.findById(container.getFacility().getId()).get();
            Facility facilityTo = facilityRepo.findByUid(orderModel.getToFacilityId());
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
                    container.setStatus(Constants.ContainerStatus.ORDERED.getStatus());
                    container.setUpdatedAt(System.currentTimeMillis());
                    Order order = new Order();
                    order.setContainer(container);
                    order.setWeight(Long.valueOf(container.getTypeContainer().getSize()));

                    if(orderModel.getFromFacilityId() != null) {
                        Facility facilityFrom = facilityRepo.findByUid(orderModel.getFromFacilityId());
                        order.setFromFacility(facilityFrom);
                    }
                    if(orderModel.getToFacilityId() != null) {
                        Facility facilityTo = facilityRepo.findByUid(orderModel.getToFacilityId());
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
        order.setCustomerId(orderModel.getUsername());
        order.setEarlyDeliveryTime(System.currentTimeMillis() + 1000*60*60*24*365);
        if(orderModel.getLateDeliveryTime() != null && orderModel.getLateDeliveryTime() != 0) {
            order.setLateDeliveryTime(orderModel.getLateDeliveryTime());
        } else {
            order.setLateDeliveryTime(System.currentTimeMillis() + 1000*60*60*24*365);
        }
        if(orderModel.getLatePickupTime() != null && orderModel.getLatePickupTime() != 0) {
            order.setLatePickupTime(orderModel.getLatePickupTime());
        } else {
            order.setLatePickupTime(System.currentTimeMillis() + 1000*60*60*24*365);
        }
        order.setEarlyPickupTime(System.currentTimeMillis() + 1000*60*60*24*365);
        order.setType(orderModel.getType());
        order.setIsBreakRomooc(orderModel.getIsBreakRomooc());
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
        if(orderFilterRequestDTO.getStatus() != null){
            sql += " AND status IN :status";
            sqlCount += " AND status IN :status";
            params.put("status", orderFilterRequestDTO.getStatus());
        }
        if(orderFilterRequestDTO.getType() != null && orderFilterRequestDTO.getType().equals("APPROVED")){
            List<String> status = new ArrayList<>();
            status.add("WAIT_APPROVE");
            status.add("CANCEL");
            sql += " AND status NOT IN :statusNotIn";
            sqlCount += " AND status NOT IN :statusNotIn";
            params.put("statusNotIn", status);
        }

        if(orderFilterRequestDTO.getType() != null && orderFilterRequestDTO.getType().equals("WAIT_APPROVE")){
            sql += " AND status = :statusType";
            sqlCount += " AND status = :statusType";
            params.put("statusType", "WAIT_APPROVE");
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
        if(orderModel.getToFacilityId() != order.getToFacility().getUid()) {
            Facility facility = facilityRepo.findByUid(orderModel.getToFacilityId());
            order.setToFacility(facility);
        }
        order.setIsBreakRomooc(orderModel.getIsBreakRomooc());
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

    @Override
    public Long countOrderByMonth(DashboardTimeOrderDTO dashboardTimeOrderDTO, String status) {
        String sqlCount = "SELECT COUNT(id) FROM container_transport_order WHERE 1=1";
        HashMap<String, Object> params = new HashMap<>();

        if(status.equals(Constants.OrderStatus.ORDERED.getStatus())) {
            sqlCount += " AND created_at >= :startTime";
            params.put("startTime", dashboardTimeOrderDTO.getStartTime());

            sqlCount += " AND created_at <= :endTime";
            params.put("endTime", dashboardTimeOrderDTO.getEndTime());
        }

        if(status.equals(Constants.OrderStatus.DONE.getStatus())) {
            sqlCount += " AND updated_at >= :startTime";
            params.put("startTime", dashboardTimeOrderDTO.getStartTime());

            sqlCount += " AND updated_at <= :endTime";
            params.put("endTime", dashboardTimeOrderDTO.getEndTime());

            sqlCount += " AND status = :status";
            params.put("status", status);
        }

        Query queryCount = this.entityManager.createNativeQuery(sqlCount);
        for (String i : params.keySet()) {
            queryCount.setParameter(i, params.get(i));
        }

        return (Long) queryCount.getSingleResult();
    }

    @Override
    public OrderModel deleteOrder(String uid) {
        Order order = orderRepo.findByUid(uid);
        Container container = order.getContainer();
        container.setStatus(Constants.ContainerStatus.AVAILABLE.getStatus());
        container.setUpdatedAt(System.currentTimeMillis());
        containerRepo.save(container);
        order.setStatus(Constants.OrderStatus.CANCEL.getStatus());
        orderRepo.save(order);
        return convertToModel(order);
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
                .facilityUid(facility.getUid())
                .longitude(facility.getLongitude())
                .latitude(facility.getLatitude())
                .processingTimePickUp(facility.getProcessingTimePickUp())
                .processingTimeDrop(facility.getProcessingTimeDrop())
                .address(facility.getAddress())
                .build();
        return facilityResponsive;
    }

    public Long getFacilityNearest(String facilityUid) {
        AtomicReference<Long> facilityRes = new AtomicReference<>();

        List<FacilityModel> facilityModels = getAllFacilityAdmin();

        Long facilityId = facilityRepo.findByUid(facilityUid).getId();

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

    public Container getContainerNearest(String toFacility, long containerSize) {
        Long toFacilityId = facilityRepo.findByUid(toFacility).getId();
        List<FacilityModel> facilityModels = getAllFacilityAdmin();

        AtomicReference<BigDecimal> distant = new AtomicReference<>(new BigDecimal(Long.MAX_VALUE));
        List<Relationship> relationships = relationshipService.getAllRelationShip();

        AtomicReference<Long> containerId = new AtomicReference<>();

        facilityModels.forEach((facilityModel) -> {
            relationships.forEach(relationship -> {
                if(relationship.getFromFacility() == facilityModel.getId()
                        && relationship.getToFacility() == toFacilityId
                        && checkContainerInDepot(facilityModel.getUid(), containerSize) != null
                ) {
                    if(relationship.getDistant().compareTo(distant.get()) < 0) {
                        distant.set(relationship.getDistant());
                        containerId.set(checkContainerInDepot(facilityModel.getUid(), containerSize).getId());
                    }
                }
            });
        });
        if(containerId.get() != null) {
            return containerRepo.findById(containerId.get()).get();
        }
        return null;
    }

    public ContainerModel checkContainerInDepot (String facilityUid, long containerSize) {
        ContainerFilterRequestDTO requestDTO = new ContainerFilterRequestDTO();
        requestDTO.setContainerSize(containerSize);
        requestDTO.setFacilityId(facilityUid);
        List<ContainerModel> containerModels = containerService.filterContainer(requestDTO).getContainerModels();
        if(containerModels != null && containerModels.size() != 0) {
            return containerModels.get(0);
        }
        return null;
    }

    public List<FacilityModel> getAllFacilityAdmin() {
        FacilityFilterRequestDTO requestDTO = new FacilityFilterRequestDTO();
        requestDTO.setTypeOwner(Arrays.asList("ADMIN"));
        requestDTO.setType("Container");
        List<FacilityModel> facilityModels = facilityService.filterFacility(requestDTO).getFacilityModels();
        return facilityModels;
    }
}
