package com.hust.wmsbackend.management.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphhopper.ResponsePath;
import com.graphhopper.util.PointList;
import com.hust.wmsbackend.management.entity.*;
import com.hust.wmsbackend.management.model.DeliveryTripDTO;
import com.hust.wmsbackend.management.model.request.NewNotificationRequest;
import com.hust.wmsbackend.management.model.response.AutoRouteResponse;
import com.hust.wmsbackend.management.repository.*;
import com.hust.wmsbackend.management.service.AutoRouteService;
import com.hust.wmsbackend.management.utils.ExternalApiCaller;
import com.hust.wmsbackend.vrp.delivery.DeliveryAddressDTO;
import com.hust.wmsbackend.vrp.delivery.DeliveryRouteService;
import com.hust.wmsbackend.vrp.delivery.RouteRequest;
import com.hust.wmsbackend.vrp.delivery.RouteResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
//@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class AutoRouteServiceImpl implements AutoRouteService {

    @Autowired
    private DeliveryRouteService deliveryRouteService;
    // repository
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private AssignedOrderItemRepository assignedOrderItemRepository;
    @Autowired
    private SaleOrderHeaderRepository saleOrderHeaderRepository;
    @Autowired
    private CustomerAddressRepository customerAddressRepository;
    @Autowired
    private DeliveryTripPathRepository deliveryTripPathRepository;
    @Autowired
    private DeliveryTripItemRepository deliveryTripItemRepository;
    @Autowired
    private DeliveryTripRepository deliveryTripRepository;
    @Value("${external-api.notification.uri}")
    private String NOTIFICATION_URI;
    @Autowired
    private ExternalApiCaller apiCaller;

    @Override
    @Transactional
    public void route(Principal principal, String token, DeliveryTripDTO request) {
        // map request to auto route service input
        List<DeliveryTripDTO.DeliveryTripItemDTO> items = request.getItems();
        if (items.size() < 1) {
            log.warn("Request to auto route is empty");
            return;
        }

        UUID warehouseId = items.get(0).getWarehouseId();
        Optional<Warehouse> warehouseOpt = warehouseRepository.findById(warehouseId);
        if (!warehouseOpt.isPresent()) {
            throw new RuntimeException(String.format("Warehouse with id %s is not exist", warehouseId));
        }
        Warehouse warehouse = warehouseOpt.get();
        List<DeliveryAddressDTO> addressDTOs = new ArrayList<>();
        for (DeliveryTripDTO.DeliveryTripItemDTO item : items) {
            CustomerAddress customerAddress = getCustomerAddressFromAssignedOrderItemId(item.getAssignOrderItemId());
            DeliveryAddressDTO adder = DeliveryAddressDTO.builder()
                                                         .deliveryTripItemId(item.getDeliveryTripItemId())
                                                         .longitude(customerAddress.getLongitude())
                                                         .latitude(customerAddress.getLatitude()).build();
            addressDTOs.add(adder);
        }

        RouteRequest routeRequest = RouteRequest.builder().warehouseLat(warehouse.getLatitude())
                                                .warehouseLon(warehouse.getLongitude()).addressDTOs(addressDTOs).build();

        // get route response
        RouteResponse routeResponse = deliveryRouteService.getRoute(routeRequest);

        // delete old paths of this delivery_trip (if exist)
        String deliveryTripId = request.getDeliveryTripId();
        deliveryTripPathRepository.deleteAllByDeliveryTripId(deliveryTripId);

        // save new paths in database
        List<ResponsePath> paths = routeResponse.getPaths();
        List<DeliveryTripPath> deliveryTripPaths = new ArrayList<>();
        for (ResponsePath path : paths) {
            PointList pointList = path.getPoints();
            int size = pointList.size();
            for (int i = 0; i < size; i++) {
                DeliveryTripPath adder = DeliveryTripPath.builder().deliveryTripId(deliveryTripId)
                                                         .longitude(BigDecimal.valueOf(pointList.getLon(i)))
                                                         .latitude(BigDecimal.valueOf(pointList.getLat(i))).build();
                deliveryTripPaths.add(adder);
            }
        }
        deliveryTripPathRepository.saveAll(deliveryTripPaths);

        // update sequence of delivery_trip_item
        List<DeliveryAddressDTO> orders = routeResponse.getOrder();
        List<DeliveryTripItem> updateItems = new ArrayList<>();
        for (DeliveryAddressDTO order : orders) {
            Optional<DeliveryTripItem> deliveryTripItemOpt = deliveryTripItemRepository.findById(order.getDeliveryTripItemId());
            if (deliveryTripItemOpt.isPresent()) {
                DeliveryTripItem deliveryTripItem = deliveryTripItemOpt.get();
                deliveryTripItem.setSequence(order.getSequence());
                updateItems.add(deliveryTripItem);
            }
        }
        deliveryTripItemRepository.saveAll(updateItems);

        // update total cost of delivery_trip
        Optional<DeliveryTrip> deliveryTripOpt = deliveryTripRepository.findById(deliveryTripId);
        if (!deliveryTripOpt.isPresent()) {
            throw new RuntimeException(String.format("Delivery trip with id %s is not exist", deliveryTripId));
        }
        DeliveryTrip deliveryTrip = deliveryTripOpt.get();
        deliveryTrip.setDistance(BigDecimal.valueOf(routeResponse.getTotalCost()));
        deliveryTripRepository.save(deliveryTrip);

        // TODO: push notifications for user
//        notificationsService.create("AUTO_ROUTE_SERVICE", principal.getName(),
//                                    String.format("Tìm hành trình tối ưu cho chuyến giao hàng %s thành công", deliveryTripId),
//                                    String.format("/delivery-manager/delivery-trips/%s", deliveryTripId));
        ObjectMapper mapper = new ObjectMapper();
        try {
            apiCaller.simpleBooleanCall(NOTIFICATION_URI, token, mapper.writeValueAsString(NewNotificationRequest.builder()
                    .toUser(principal.getName())
                    .url(String.format("/delivery-manager/delivery-trips/%s", deliveryTripId))
                    .content(String.format("Tìm hành trình tối ưu cho chuyến giao hàng %s thành công", deliveryTripId))
                    .build()));
            log.info("Push notification done");
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            log.info("Push notification fail");
        }
        log.info("Done auto route");
    }

    private CustomerAddress getCustomerAddressFromAssignedOrderItemId(UUID assignedOrderItemId) {
        Optional<AssignedOrderItem> assignedOrderItemOpt = assignedOrderItemRepository.findById(assignedOrderItemId);
        if (!assignedOrderItemOpt.isPresent()) {
            throw new RuntimeException(String.format("Assigned order item with id %s is not exist",
                                                     assignedOrderItemId));
        }

        Optional<SaleOrderHeader> saleOrderHeaderOpt = saleOrderHeaderRepository.findById(assignedOrderItemOpt.get().getOrderId());
        if (!saleOrderHeaderOpt.isPresent()) {
            throw new RuntimeException(String.format("Sale order header with id %s is not exist",
                                                     assignedOrderItemOpt.get().getOrderId()));
        }

        Optional<CustomerAddress> customerAddressOpt = customerAddressRepository.findById(saleOrderHeaderOpt.get()
                                                                                                            .getCustomerAddressId());
        if (!customerAddressOpt.isPresent()) {
            throw new RuntimeException(String.format("Customer address with id %s is not exist",
                                                     saleOrderHeaderOpt.get().getCustomerAddressId()));
        }
        CustomerAddress customerAddress = customerAddressOpt.get();
        return customerAddress;
    }

    @Override
    public AutoRouteResponse getPath(String deliveryTripId) {
        Optional<DeliveryTrip> deliveryTripOpt = deliveryTripRepository.findById(deliveryTripId);
        if (!deliveryTripOpt.isPresent()) {
            log.warn(String.format("Delivery trip with id %s is not exist", deliveryTripId));
            return null;
        }
        DeliveryTrip deliveryTrip = deliveryTripOpt.get();
        Optional<Warehouse> warehouseOpt = warehouseRepository.findById(deliveryTrip.getWarehouseId());
        if (!warehouseOpt.isPresent()) {
            log.warn(String.format("Warehouse with id %s is not exist", deliveryTrip.getWarehouseId()));
            return null;
        }
        Warehouse warehouse = warehouseOpt.get();
        AutoRouteResponse.Marker warehouseMarker = AutoRouteResponse.Marker.builder()
            .name(warehouse.getName()).position(Arrays.asList(warehouse.getLatitude(), warehouse.getLongitude())).build();

        List<DeliveryTripPath> paths = deliveryTripPathRepository.findAllByDeliveryTripId(deliveryTripId);
        List<AutoRouteResponse.Point> points = paths.stream().map(point ->
            AutoRouteResponse.Point.builder().lat(point.getLatitude()).lon(point.getLongitude()).build())
            .collect(Collectors.toList());

        List<AutoRouteResponse.Marker> customers = new ArrayList<>();
        List<DeliveryTripItem> items = deliveryTripItemRepository.findAllByDeliveryTripIdAndIsDeletedIsFalse(deliveryTripId);
        for (DeliveryTripItem item : items) {
            Optional<AssignedOrderItem> assignedOrderItemOpt = assignedOrderItemRepository.findById(item.getAssignedOrderItemId());
            if (!assignedOrderItemOpt.isPresent()) {
                log.warn(String.format("Assigned order item with id %s is not exist", item.getAssignedOrderItemId()));
                return null;
            }
            AssignedOrderItem assignedOrderItem = assignedOrderItemOpt.get();
            Optional<SaleOrderHeader> saleOrderHeaderOpt = saleOrderHeaderRepository.findById(assignedOrderItem.getOrderId());
            if (!saleOrderHeaderOpt.isPresent()) {
                throw new RuntimeException(String.format("Sale order header with id %s is not exist",
                                                         assignedOrderItemOpt.get().getOrderId()));
            }
            SaleOrderHeader saleOrderHeader = saleOrderHeaderOpt.get();
            Optional<CustomerAddress> customerAddressOpt = customerAddressRepository.findById(saleOrderHeader.getCustomerAddressId());
            if (!customerAddressOpt.isPresent()) {
                throw new RuntimeException(String.format("Customer address with id %s is not exist",
                                                         saleOrderHeaderOpt.get().getCustomerAddressId()));
            }
            String CUSTOMER_NAME_MARKER_TEMPLATE = "STT: %d - %s - %s";
            CustomerAddress customerAddress = customerAddressOpt.get();
            AutoRouteResponse.Marker adder = AutoRouteResponse.Marker.builder().name(String.format(CUSTOMER_NAME_MARKER_TEMPLATE, item.getSequence(), saleOrderHeader.getCustomerName(), saleOrderHeader.getCustomerPhoneNumber()))
                .position(Arrays.asList(customerAddress.getLatitude(), customerAddress.getLongitude())).build();
            customers.add(adder);
        }
        return AutoRouteResponse.builder().deliveryTripId(deliveryTripId).points(points)
            .customers(customers).warehouse(warehouseMarker).build();
    }


}
