package com.hust.wmsbackend.management.service.service2.serviceImplement;

import com.graphhopper.ResponsePath;
import com.graphhopper.util.PointList;
import com.hust.wmsbackend.management.entity.*;
import com.hust.wmsbackend.management.model.DeliveryTripDTO;
import com.hust.wmsbackend.management.model.response.AutoRouteResponse;
import com.hust.wmsbackend.management.repository.*;
import com.hust.wmsbackend.management.repository.repo2.CustomerAddressRepository2;
import com.hust.wmsbackend.management.repository.repo2.DeliveryTripItemRepository2;
import com.hust.wmsbackend.management.repository.repo2.SaleOrderHeaderRepository2;
import com.hust.wmsbackend.management.repository.repo2.WarehouseRepository2;
import com.hust.wmsbackend.management.service.service2.AutoRouteService;
import com.hust.wmsbackend.management.auto.algorithms.DistanceCalculator2;
import com.hust.wmsbackend.management.service.NotificationsService;
import com.hust.wmsbackend.vrp.delivery.DeliveryAddressDTO;
import com.hust.wmsbackend.vrp.delivery.DeliveryRouteService;
import com.hust.wmsbackend.vrp.delivery.RouteRequest;
import com.hust.wmsbackend.vrp.delivery.RouteResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
//@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class AutoRouteServiceImplVD implements AutoRouteService {

    @Autowired
    private DeliveryRouteService deliveryRouteService;
    // repository
    @Autowired
    private WarehouseRepository2 warehouseRepository;
    @Autowired
    private AssignedOrderItemRepository assignedOrderItemRepository;
    @Autowired
    private SaleOrderHeaderRepository2 saleOrderHeaderRepository;
    @Autowired
    private CustomerAddressRepository2 customerAddressRepository;
    @Autowired
    private DeliveryTripPathRepository deliveryTripPathRepository;
    @Autowired
    private DeliveryTripItemRepository2 deliveryTripItemRepository;
    @Autowired
    private DeliveryTripRepository deliveryTripRepository;
    @Autowired
    private NotificationsService notificationsService;

    @Autowired
    DistanceCalculator2 distanceCalculator;

    @Override
    @Transactional
    public void route(Principal principal, String token, DeliveryTripDTO request) {
        log.info("Start auto route");
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
        RouteResponse routeResponse;
        try {
            routeResponse = deliveryRouteService.getRoute(routeRequest);
        } catch (RuntimeException e) {
            String deliveryTripId = request.getDeliveryTripId();
            notificationsService.create("AUTO_ROUTE", principal.getName(),
                    String.format("Không tìm được lộ trình cho chuyến giao hàng %s", deliveryTripId),
                    String.format("/delivery-manager/delivery-trips/%s", deliveryTripId));
            log.info("Fail auto route");
            return;
        }

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

        notificationsService.create("AUTO_ROUTE", principal.getName(),
            String.format("Tìm hành trình tối ưu cho chuyến giao hàng %s thành công", deliveryTripId),
            String.format("/delivery-manager/delivery-trips/%s", deliveryTripId));
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
        if (deliveryTrip.getWarehouseId() == null) {
            log.info("Delivery trip %s has no path");
            return null;
        }
        Optional<Warehouse> warehouseOpt = warehouseRepository.findById(deliveryTrip.getWarehouseId());
        if (!warehouseOpt.isPresent()) {
            log.warn(String.format("Warehouse with id %s is not exist", deliveryTrip.getWarehouseId()));
            return null;
        }

        Warehouse warehouse = warehouseOpt.get();
        AutoRouteResponse.Marker warehouseMarker = AutoRouteResponse.Marker.builder()
            .name(warehouse.getName())
                .position(Arrays.asList(warehouse.getLatitude(), warehouse.getLongitude()))
                .build();

        List<DeliveryTripPath> paths = deliveryTripPathRepository.findAllByDeliveryTripId(deliveryTripId);
        List<AutoRouteResponse.Point> points = paths.stream().map(point ->
            AutoRouteResponse.Point.builder()
                    .lat(point.getLatitude())
                    .lon(point.getLongitude())
                    .build())
            .collect(Collectors.toList());

        List<AutoRouteResponse.Marker> customers = new ArrayList<>();
        List<DeliveryTripItem> items = deliveryTripItemRepository.findAllByDeliveryTripIdAndIsDeletedIsFalse(deliveryTripId);
        // build List<Marker> customers
        for (DeliveryTripItem item : items) {
            // get assignedOrderItem
            Optional<AssignedOrderItem> assignedOrderItemOpt = assignedOrderItemRepository.findById(item.getAssignedOrderItemId());
            if (!assignedOrderItemOpt.isPresent()) {
                log.warn(String.format("Assigned order item with id %s is not exist", item.getAssignedOrderItemId()));
                return null;
            }
            AssignedOrderItem assignedOrderItem = assignedOrderItemOpt.get();

            // get saleOrderHeader
            Optional<SaleOrderHeader> saleOrderHeaderOpt = saleOrderHeaderRepository.findById(assignedOrderItem.getOrderId());
            if (!saleOrderHeaderOpt.isPresent()) {
                throw new RuntimeException(String.format("Sale order header with id %s is not exist",
                                                         assignedOrderItemOpt.get().getOrderId()));
            }
            SaleOrderHeader saleOrderHeader = saleOrderHeaderOpt.get();

            // get customerAddress
            Optional<CustomerAddress> customerAddressOpt = customerAddressRepository.findById(saleOrderHeader.getCustomerAddressId());
            if (!customerAddressOpt.isPresent()) {
                throw new RuntimeException(String.format("Customer address with id %s is not exist",
                                                         saleOrderHeaderOpt.get().getCustomerAddressId()));
            }
            String CUSTOMER_NAME_MARKER_TEMPLATE = "STT: %d - %s - %s";
            CustomerAddress customerAddress = customerAddressOpt.get();
            AutoRouteResponse.Marker adder = AutoRouteResponse.Marker.builder()
                .name(String.format(CUSTOMER_NAME_MARKER_TEMPLATE,
                        item.getSequence(),
                        saleOrderHeader.getCustomerName(),
                        saleOrderHeader.getCustomerPhoneNumber()))
                .position(Arrays.asList(customerAddress.getLatitude(), customerAddress.getLongitude()))
                .sequence(item.getSequence())
                .build();
            customers.add(adder);
        }

        return AutoRouteResponse.builder().deliveryTripId(deliveryTripId).points(points)
            .customers(customers).warehouse(warehouseMarker).build();
    }


    public List<DeliveryAddressDTO> buildNormAddressList(List<DeliveryAddressDTO> originAddress) {
        log.info(String.format("Origin address => %s", originAddress));
        List<DeliveryAddressDTO> normAddress = new ArrayList<>();
        normAddress.addAll(originAddress);
        Set<Integer> duplicateIndex = new HashSet<>();
        for (int i = 0; i < originAddress.size() - 1; i++) {
            for (int j = i + 1; j < originAddress.size(); j++) {
                if (originAddress.get(i).getLatitude().compareTo(originAddress.get(j).getLatitude()) == 0 &&
                        originAddress.get(i).getLongitude().compareTo(originAddress.get(j).getLongitude()) == 0) {
                    duplicateIndex.add(j);
                }
            }
        }
        List<Integer> sortedIndex = new ArrayList<>(duplicateIndex);
        Collections.sort(sortedIndex);
        for (int i = sortedIndex.size() - 1; i >= 0; i--) {
            int removeIndex = sortedIndex.get(i);
            normAddress.remove(removeIndex);
        }
        log.info(String.format("Origin address done => %s", normAddress));
        return normAddress;
    }

    public RouteResponse solveTSP(RouteRequest request) {
        log.info("Start TSP algo" + request.getAddressDTOs().size());
        int customerCount = request.getAddressDTOs().size();
        List<DeliveryAddressDTO> initOrder = request.getAddressDTOs();

        double totalCost = 0.0;
        List<ResponsePath> responsePath = new ArrayList<>();
        List<DeliveryAddressDTO> order = new ArrayList<>();


        List<Integer> checkVisitedAddress = new ArrayList<>();
        checkVisitedAddress.add(1); // checkVisitedAddress[0] => warehouse => 1
        for (int i = 1; i <= customerCount; i++) {
            checkVisitedAddress.add(0);
        }

        // chon dia diem 1
        List<ResponsePath> tmpResponsePath = new ArrayList<>();
        double shortestDistance = Double.MAX_VALUE;
        ResponsePath shortestPath = null;
        int currentIndex = -1;

        for (int i = 0; i < customerCount; i++) { // index = stt - 1
            DeliveryAddressDTO o = initOrder.get(i);
            ResponsePath currentPath = distanceCalculator.calculate(request.getWarehouseLat(), request.getWarehouseLon(),
                    initOrder.get(i).getLatitude(), initOrder.get(i).getLongitude());

            if (currentPath != null && currentPath.getDistance() < shortestDistance) {
                shortestPath = currentPath; // Cập nhật đường đi ngắn nhất
                shortestDistance = currentPath.getDistance(); // Cập nhật khoảng cách ngắn nhất
                currentIndex = i;
            }
        }
        log.info("place 1 = " + currentIndex);
        responsePath.add(shortestPath);
        totalCost += shortestDistance;
        order.add(initOrder.get(currentIndex));
        checkVisitedAddress.set(currentIndex + 1, 1);

        // chon cac dia diem tiep theo
        for (int i = 2; i <= customerCount; i++) {
            log.info("visited check list: " + checkVisitedAddress);
            double minDistance = Double.MAX_VALUE;
            ResponsePath minPath = null;
            int minIndex = -1;

            for (int j = 1; j <= customerCount; j++) {
                if (checkVisitedAddress.get(j) == 0) {
                    ResponsePath currentPath = distanceCalculator.calculate(
                            order.get(i - 2).getLatitude(), order.get(i - 2).getLongitude(),
                            initOrder.get(j - 1).getLatitude(), initOrder.get(j - 1).getLongitude());

                    if (currentPath != null && currentPath.getDistance() < minDistance) {
                        minPath = currentPath;
                        minDistance = currentPath.getDistance();
                        minIndex = j;
                    }
                }
            }

            if (minIndex == -1) {
                break;
            }

            responsePath.add(minPath);
            totalCost += minDistance;
            order.add(initOrder.get(minIndex - 1));
            checkVisitedAddress.set(minIndex, 1);
            log.info("place " + i + " has index" + minIndex);
        }

        RouteResponse response = new RouteResponse();
        response.setTotalCost(totalCost);
        response.setPaths(responsePath);
        response.setOrder(order);
        return response;
    }
    @Override
    @Transactional
    public AutoRouteResponse autoRoute2(DeliveryTripDTO request, Principal principal) {
        log.info("Start auto route");

        List<DeliveryTripDTO.DeliveryTripItemDTO> items = request.getItems();
        if (items.size() < 1) {
            log.warn("Request to auto route is empty");
            return null;
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

        List<DeliveryAddressDTO> addressDTOsNoDuplicate = buildNormAddressList(addressDTOs);

        // Khởi tạo request
        RouteRequest routeRequest = RouteRequest.builder().warehouseLat(warehouse.getLatitude())
                .warehouseLon(warehouse.getLongitude()).addressDTOs(addressDTOsNoDuplicate).build();
        log.info(String.format("Start TSP solver => %s", routeRequest));

        RouteResponse routeResponse;
        routeResponse = solveTSP(routeRequest);
        log.info(String.format("Done calculate route 1 => %s", routeResponse.getOrder()));

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
        int initSequence = 1;
        for( DeliveryAddressDTO od : orders) {
            od.setSequence(initSequence);
            initSequence++;
        }
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

        // build response
        AutoRouteResponse.Marker warehouseMarker = AutoRouteResponse.Marker.builder()
                .name(warehouse.getName())
                .position(Arrays.asList(warehouse.getLatitude(), warehouse.getLongitude()))
                .build();

        List<AutoRouteResponse.Point> points = deliveryTripPaths.stream().map(point ->
                        AutoRouteResponse.Point.builder()
                                .lat(point.getLatitude())
                                .lon(point.getLongitude())
                                .build())
                .collect(Collectors.toList());

        List<AutoRouteResponse.Marker> customers = new ArrayList<>();

        // build List<Marker> customers
        for (DeliveryTripItem item : updateItems) {
            // get assignedOrderItem
            Optional<AssignedOrderItem> assignedOrderItemOpt = assignedOrderItemRepository.findById(item.getAssignedOrderItemId());
            if (!assignedOrderItemOpt.isPresent()) {
                log.warn(String.format("Assigned order item with id %s is not exist", item.getAssignedOrderItemId()));
                return null;
            }
            AssignedOrderItem assignedOrderItem = assignedOrderItemOpt.get();

            // get saleOrderHeader
            Optional<SaleOrderHeader> saleOrderHeaderOpt = saleOrderHeaderRepository.findById(assignedOrderItem.getOrderId());
            if (!saleOrderHeaderOpt.isPresent()) {
                throw new RuntimeException(String.format("Sale order header with id %s is not exist",
                        assignedOrderItemOpt.get().getOrderId()));
            }
            SaleOrderHeader saleOrderHeader = saleOrderHeaderOpt.get();

            // get customerAddress
            Optional<CustomerAddress> customerAddressOpt = customerAddressRepository.findById(saleOrderHeader.getCustomerAddressId());
            if (!customerAddressOpt.isPresent()) {
                throw new RuntimeException(String.format("Customer address with id %s is not exist",
                        saleOrderHeaderOpt.get().getCustomerAddressId()));
            }
            String CUSTOMER_NAME_MARKER_TEMPLATE = "STT: %d - %s - %s";
            CustomerAddress customerAddress = customerAddressOpt.get();
            AutoRouteResponse.Marker adder = AutoRouteResponse.Marker.builder()
                    .name(String.format(CUSTOMER_NAME_MARKER_TEMPLATE,
                            item.getSequence(),
                            saleOrderHeader.getCustomerName(),
                            saleOrderHeader.getCustomerPhoneNumber()))
                    .position(Arrays.asList(customerAddress.getLatitude(), customerAddress.getLongitude()))
                    .sequence(item.getSequence())
                    .build();
            customers.add(adder);
        }

        notificationsService.create("AUTO_ROUTE", principal.getName(),
                String.format("Tìm hành trình tối ưu cho chuyến giao hàng %s thành công", deliveryTripId),
                String.format("/delivery-manager/delivery-trips/%s", deliveryTripId));
        log.info("Done auto route");

        return AutoRouteResponse.builder().deliveryTripId(deliveryTripId).points(points)
                .customers(customers).warehouse(warehouseMarker).build();
    }

}
