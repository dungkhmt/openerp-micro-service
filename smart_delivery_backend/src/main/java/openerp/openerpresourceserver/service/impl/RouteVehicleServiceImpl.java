package openerp.openerpresourceserver.service.impl;


import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.dto.RouteVehicleDetailDto;
import openerp.openerpresourceserver.dto.RouteVehicleDto;
import openerp.openerpresourceserver.entity.Route;
import openerp.openerpresourceserver.entity.RouteVehicle;
import openerp.openerpresourceserver.entity.Vehicle;
import openerp.openerpresourceserver.entity.enumentity.VehicleStatus;
import openerp.openerpresourceserver.mapper.RouteVehicleMapper;
import openerp.openerpresourceserver.repository.OrderRepo;
import openerp.openerpresourceserver.repository.RouteRepository;
import openerp.openerpresourceserver.repository.RouteVehicleRepository;
import openerp.openerpresourceserver.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import openerp.openerpresourceserver.service.RouteVehicleService;
import java.time.Instant;
import java.util.ArrayList;
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
    private final RouteVehicleMapper routeVehicleMapper = RouteVehicleMapper.INSTANCE;
    /**
     * Phân công xe cho tuyến đường
     */
    @Transactional
    @Override
    public RouteVehicle assignVehicleToRoute(UUID routeId, UUID vehicleId) {
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
        routeVehicle.setCreatedAt(Instant.now());
        routeVehicle.setUpdatedAt(Instant.now());

        // Cập nhật trạng thái xe

        vehicleRepo.save(vehicle);

        return routeVehicleRepository.save(routeVehicle);
    }

    /**
     * Cập nhật thông tin phân công
     */
    @Transactional
    @Override
    public RouteVehicle updateRouteVehicle(UUID id) {
        RouteVehicle routeVehicle = routeVehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Route vehicle assignment not found"));

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
    public List<RouteVehicleDetailDto> getRouteVehiclesByVehicle(UUID vehicleId) {
        if (!vehicleRepo.existsById(vehicleId)) {
            throw new NotFoundException("Vehicle not found");
        }
         return routeVehicleRepository.findDetailByVehicleId(vehicleId);
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
    /**
     * Lấy thông tin tất cả chuyến xe đã gán
     */
    @Override
    public List<RouteVehicleDto> getAllVehicleAssignments() {
        List<RouteVehicle> routeVehicles =  routeVehicleRepository.findAll();
        if(routeVehicles.isEmpty()) throw new NotFoundException("Not found any routeVehicles");
        List<RouteVehicleDto> routeVehicleDtos = new ArrayList<>();
        for(RouteVehicle routeVehicle : routeVehicles){
            routeVehicleDtos.add(routeVehicleMapper.routeVehicleToRouteVehicleDto(routeVehicle));
        }
        return routeVehicleDtos;
    }

}