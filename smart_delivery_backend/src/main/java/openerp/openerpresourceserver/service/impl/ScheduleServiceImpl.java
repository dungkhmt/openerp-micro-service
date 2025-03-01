package openerp.openerpresourceserver.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.entity.Route;
import openerp.openerpresourceserver.entity.RouteStop;
import openerp.openerpresourceserver.entity.RouteVehicle;
import openerp.openerpresourceserver.repository.RouteRepository;
import openerp.openerpresourceserver.repository.RouteStopRepository;
import openerp.openerpresourceserver.repository.RouteVehicleRepository;
import openerp.openerpresourceserver.service.ScheduleService;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final RouteVehicleRepository routeVehicleRepository;
    private final RouteRepository routeRepository;
    private final RouteStopRepository routeStopRepository;
    /**
     * Lấy lịch trình trong khoảng thời gian
     */
    @Override
    public List<RouteVehicle> getScheduleByDateRange(Instant startDate, Instant endDate) {
        return routeVehicleRepository.findByDateRange(startDate, endDate);
    }

    /**
     * Lấy lịch trình xe trong tuần
     */
    @Override
    public List<RouteVehicle> getVehicleWeeklySchedule(UUID vehicleId) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        Instant startInstant = startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = endOfWeek.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        return routeVehicleRepository.findByVehicleId(vehicleId).stream()
                .filter(rv -> isInDateRange(rv.getCreatedAt(), startInstant, endInstant))
                .collect(Collectors.toList());
    }

    /**
     * Lấy lịch trình tài xế trong tuần
     */
    @Override
    public List<RouteVehicle> getDriverWeeklySchedule(UUID driverId) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        Instant startInstant = startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = endOfWeek.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        return routeVehicleRepository.findByDriverId(driverId).stream()
                .filter(rv -> isInDateRange(rv.getCreatedAt(), startInstant, endInstant))
                .collect(Collectors.toList());
    }

    /**
     * Tìm tuyến đường phù hợp cho đơn hàng
     */
    @Override
    public List<Route> findSuitableRoutesForOrder(UUID originHubId, UUID destinationHubId) {
        List<Route> allRoutes = routeRepository.findAll();

        return allRoutes.stream()
                .filter(route -> {
                    // Lấy các điểm dừng của tuyến đường
                    List<UUID> hubsOnRoute = routeStopRepository.findByRouteIdOrderByStopSequence(route.getRouteId())
                            .stream()
                            .map(RouteStop::getHubId)
                            .collect(Collectors.toList());

                    // Kiểm tra tuyến đường có đi qua cả originHub và destinationHub không
                    return hubsOnRoute.contains(originHubId) && hubsOnRoute.contains(destinationHubId);
                })
                .collect(Collectors.toList());
    }

    /**
     * Kiểm tra thời gian trong khoảng
     */
    @Override
    public boolean isInDateRange(Instant date, Instant start, Instant end) {
        return !date.isBefore(start) && !date.isAfter(end);
    }
}
