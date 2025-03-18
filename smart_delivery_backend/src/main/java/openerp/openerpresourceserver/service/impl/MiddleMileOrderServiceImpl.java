package openerp.openerpresourceserver.service.impl;


import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.dto.OrderSummaryDTO;
import openerp.openerpresourceserver.dto.OrderSummaryMiddleMileDto;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.RouteVehicle;
import openerp.openerpresourceserver.entity.Vehicle;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.entity.enumentity.RouteDirection;
import openerp.openerpresourceserver.entity.enumentity.VehicleStatus;
import openerp.openerpresourceserver.repository.OrderRepo;
import openerp.openerpresourceserver.repository.RouteVehicleRepository;
import openerp.openerpresourceserver.repository.VehicleRepository;
import openerp.openerpresourceserver.service.MiddleMileOrderService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MiddleMileOrderServiceImpl implements MiddleMileOrderService {

    private final OrderRepo orderRepo;
    private final RouteVehicleRepository routeVehicleRepository;
    private final VehicleRepository vehicleRepo;

    /**
     * Gán đơn hàng cho một chuyến
     */
    @Transactional
    @Override
    public void assignOrdersToTrip(UUID routeVehicleId, List<UUID> orderIds) {
        RouteVehicle routeVehicle = routeVehicleRepository.findById(routeVehicleId)
                .orElseThrow(() -> new NotFoundException("Route vehicle assignment not found"));
        Vehicle vehicle = vehicleRepo.findById(routeVehicle.getVehicleId()).orElseThrow(()-> new NotFoundException("not found vehicle"));


        List<Order> orders = new ArrayList<>();

        for (UUID orderId : orderIds) {
            Order order = orderRepo.findById(orderId)
                    .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

            // Kiểm tra đơn hàng có ở trạng thái hợp lệ không
            if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.ASSIGNED) {
                throw new IllegalStateException("Order is not in a valid state for assignment: " + orderId);
            }

            orders.add(order);
        }

        // Gán đơn hàng cho chuyến
        for (Order order : orders) {
            order.setRouteId(routeVehicle.getRouteId());
            order.setVehicleId(routeVehicle.getVehicleId());
            order.setVehicleType(vehicle.getVehicleType());
            order.setVehicleLicensePlate(vehicle.getPlateNumber());
            order.setDriverId(vehicle.getDriverId());
            order.setDriverName(vehicle.getDriverName());
            order.setStatus(OrderStatus.DELIVERING);

            orderRepo.save(order);
        }
    }

    /**
     * Hủy gán đơn hàng cho chuyến
     */
    @Transactional
    @Override
    public void unassignOrderFromTrip(UUID orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

        // Kiểm tra đơn hàng có được gán cho chuyến nào không
        if (order.getRouteId() == null || order.getVehicleId() == null) {
            throw new IllegalStateException("Order is not assigned to a trip: " + orderId);
        }

        // Hủy gán đơn hàng
        order.setRouteId(null);
        order.setVehicleId(null);
        order.setVehicleType(null);
        order.setVehicleLicensePlate(null);
        order.setDriverId(null);
        order.setDriverName(null);

        // Đặt lại trạng thái nếu đang ở trạng thái SHIPPING
        if (order.getStatus() == OrderStatus.DELIVERING) {
            order.setStatus(OrderStatus.ASSIGNED);
        }

        orderRepo.save(order);
    }

    /**
     * Lấy danh sách đơn hàng theo chuyến
     */
    @Override
    public List<Order> getOrdersByTrip(UUID routeVehicleId) {
        RouteVehicle routeVehicle = routeVehicleRepository.findById(routeVehicleId)
                .orElseThrow(() -> new NotFoundException("Route vehicle assignment not found"));

        return orderRepo.findAll().stream()
                .filter(order -> routeVehicle.getRouteId().equals(order.getRouteId()) &&
                        routeVehicle.getVehicleId().equals(order.getVehicleId()))
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật trạng thái đơn hàng trong chuyến
     */
    @Transactional
    @Override
    public void updateOrderStatus(UUID orderId, OrderStatus status) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        // Kiểm tra trạng thái hợp lệ
        if (order.getStatus() == OrderStatus.DELIVERING && status == OrderStatus.DELIVERED) {
            order.setStatus(status);
            orderRepo.save(order);
        } else {
            throw new IllegalStateException("Invalid status transition");
        }
    }

    /**
     * Hoàn thành chuyến và cập nhật tất cả đơn hàng
     */
    @Transactional
    @Override
    public void completeTrip(UUID routeVehicleId) {
        List<Order> orders = getOrdersByTrip(routeVehicleId);

        for (Order order : orders) {
            // Chỉ cập nhật đơn hàng đang ở trạng thái SHIPPING
            if (order.getStatus() == OrderStatus.DELIVERING) {
                order.setStatus(OrderStatus.DELIVERED);
                orderRepo.save(order);
            }
        }

        // Cập nhật trạng thái xe
        RouteVehicle routeVehicle = routeVehicleRepository.findById(routeVehicleId)
                .orElseThrow(() -> new NotFoundException("Route vehicle assignment not found"));
        Vehicle vehicle = vehicleRepo.findById(routeVehicle.getVehicleId()).orElseThrow(()-> new NotFoundException("not found vehicle"));
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicleRepo.save(vehicle);
    }

    @Override
    public List<OrderSummaryMiddleMileDto> getCollectedHubListVehicle(UUID vehicleId, UUID hubId){
        return orderRepo.getCollectedCollectorListVehicle(vehicleId, hubId);

    }
}