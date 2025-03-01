package openerp.openerpresourceserver.service.impl;


import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.entity.Route;
import openerp.openerpresourceserver.entity.RouteVehicle;
import openerp.openerpresourceserver.entity.Vehicle;
import openerp.openerpresourceserver.entity.enumentity.VehicleStatus;
import openerp.openerpresourceserver.repository.OrderRepo;
import openerp.openerpresourceserver.repository.RouteRepository;
import openerp.openerpresourceserver.repository.RouteVehicleRepository;
import openerp.openerpresourceserver.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import openerp.openerpresourceserver.service.RouteVehicleService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RouteVehicleServiceImpl implements RouteVehicleService {

    private final RouteVehicleRepository routeVehicleRepository;
    private final RouteRepository routeRepository;
    private final VehicleRepository vehicleRepo;
    private final OrderRepo orderRepo;

    /**
     * Phân công xe cho tuyến đường
     */
    @Transactional
    @Override
    public RouteVehicle assignVehicleToRoute(UUID routeId, UUID vehicleId, String direction) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new NotFoundException("Route not found"));

        Vehicle vehicle = vehicleRepo.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Vehicle not found"));

        if (vehicle.getStatus() == VehicleStatus.ASSIGNED ||
                vehicle.getStatus() == VehicleStatus.TRANSITING) {
            throw new IllegalStateException("Vehicle is not available");
        }

        RouteVehicle routeVehicle = new RouteVehicle();
        routeVehicle.setRouteId(route.getRouteId());
        routeVehicle.setVehicleId(vehicle.getVehicleId());
        routeVehicle.setDirection(direction);
        routeVehicle.setCreatedAt(Instant.now());
        routeVehicle.setUpdatedAt(Instant.now());

        // Cập nhật trạng thái xe
        vehicle.setStatus(VehicleStatus.ASSIGNED);
        vehicleRepo.save(vehicle);

        return routeVehicleRepository.save(routeVehicle);
    }

    /**
     * Cập nhật thông tin phân công
     */
    @Transactional
    @Override
    public RouteVehicle updateRouteVehicle(UUID id, String direction) {
        RouteVehicle routeVehicle = routeVehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Route vehicle assignment not found"));

        routeVehicle.setDirection(direction);
        routeVehicle.setUpdatedAt(Instant.now());

        return routeVehicleRepository.save(routeVehicle);
    }

    /**
     * Hủy phân công xe cho tuyến đường
     */
    @Transactional
    @Override
    public void unassignVehicle(UUID routeVehicleId) {
        RouteVehicle routeVehicle = routeVehicleRepository.findById(routeVehicleId)
                .orElseThrow(() -> new NotFoundException("Route vehicle assignment not found"));

        // Kiểm tra xem có đơn hàng nào đang được gán cho xe này không
        boolean hasAssignedOrders = orderRepo.findAll().stream()
                .anyMatch(order -> routeVehicle.getVehicleId().equals(order.getVehicleId()) &&
                        routeVehicle.getRouteId().equals(order.getRouteId()));

        if (hasAssignedOrders) {
            throw new IllegalStateException("Cannot unassign vehicle with assigned orders");
        }

        // Cập nhật trạng thái xe
        Vehicle vehicle = vehicleRepo.findById(routeVehicle.getVehicleId()).orElseThrow(()-> new NotFoundException("not found vehicle"));
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicleRepo.save(vehicle);

        routeVehicleRepository.deleteById(routeVehicleId);
    }

    /**
     * Lấy tất cả phân công theo tuyến đường
     */
    @Override
    public List<RouteVehicle> getRouteVehiclesByRoute(UUID routeId) {
        if (!routeRepository.existsById(routeId)) {
            throw new NotFoundException("Route not found");
        }
        return routeVehicleRepository.findByRouteId(routeId);
    }

    /**
     * Lấy tất cả phân công theo xe
     */
    @Override
    public List<RouteVehicle> getRouteVehiclesByVehicle(UUID vehicleId) {
        if (!vehicleRepo.existsById(vehicleId)) {
            throw new NotFoundException("Vehicle not found");
        }
        return routeVehicleRepository.findByVehicleId(vehicleId);
    }

    /**
     * Lấy tất cả phân công theo tài xế
     */
    @Override
    public List<RouteVehicle> getRouteVehiclesByDriver(UUID driverId) {
        return routeVehicleRepository.findByDriverId(driverId);
    }

    /**
     * Lấy phân công trong khoảng thời gian
     */
    @Override
    public List<RouteVehicle> getRouteVehiclesByDateRange(Instant startDate, Instant endDate) {
        return routeVehicleRepository.findByDateRange(startDate, endDate);
    }

    /**
     * Lấy chi tiết phân công
     */
    @Override
    public RouteVehicle getRouteVehicleById(UUID id) {
        return routeVehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Route vehicle assignment not found"));
    }
}