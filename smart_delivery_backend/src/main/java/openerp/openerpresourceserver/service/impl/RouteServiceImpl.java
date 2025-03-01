package openerp.openerpresourceserver.service.impl;


import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.entity.Hub;
import openerp.openerpresourceserver.entity.Route;
import openerp.openerpresourceserver.entity.RouteStop;
import openerp.openerpresourceserver.repository.HubRepo;
import openerp.openerpresourceserver.repository.RouteRepository;
import openerp.openerpresourceserver.repository.RouteStopRepository;
import openerp.openerpresourceserver.service.RouteService;
import openerp.openerpresourceserver.utils.DistanceCalculator.HaversineDistanceCalculator;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final RouteStopRepository routeStopRepository;
    private final HubRepo hubRepo;

    /**
     * Tạo tuyến đường mới
     */
    @Override
    @Transactional
    public Route createRoute(Route route, List<RouteStop> stops) {
        if (routeRepository.findByRouteCode(route.getRouteCode()).isPresent()) {
            throw new IllegalArgumentException("Route code already exists: " + route.getRouteCode());
        }

        Route savedRoute = routeRepository.save(route);

        for (RouteStop stop : stops) {
            if (!hubRepo.existsById(stop.getHubId())) {
                throw new NotFoundException("Hub not found with ID: " + stop.getHubId());
            }
            stop.setRouteId(savedRoute.getRouteId());
            routeStopRepository.save(stop);
        }

        updateRouteMetrics(savedRoute.getRouteId());
        return routeRepository.findById(savedRoute.getRouteId())
                .orElseThrow(() -> new NotFoundException("Route not found"));
    }

    /**
     * Cập nhật tuyến đường
     */
    @Override
    @Transactional
    public Route updateRoute(UUID routeId, Route routeDetails, List<RouteStop> stops) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new NotFoundException("Route not found"));

        if (!route.getRouteCode().equals(routeDetails.getRouteCode()) &&
                routeRepository.findByRouteCode(routeDetails.getRouteCode()).isPresent()) {
            throw new IllegalArgumentException("Route code already exists");
        }

        route.setRouteCode(routeDetails.getRouteCode());
        route.setRouteName(routeDetails.getRouteName());
        route.setDescription(routeDetails.getDescription());
        route.setNotes(routeDetails.getNotes());
        route.setStatus(routeDetails.getStatus());

        routeRepository.save(route);

        // Xóa các điểm dừng cũ
        routeStopRepository.deleteByRouteId(routeId);

        // Thêm các điểm dừng mới
        for (RouteStop stop : stops) {
            stop.setRouteId(routeId);
            routeStopRepository.save(stop);
        }

        updateRouteMetrics(routeId);
        return routeRepository.findById(routeId)
                .orElseThrow(() -> new NotFoundException("Route not found"));
    }

    /**
     * Lấy tuyến đường theo ID
     */
    @Override
    public Route getRouteById(UUID routeId) {
        return routeRepository.findById(routeId)
                .orElseThrow(() -> new NotFoundException("Route not found"));
    }

    /**
     * Lấy danh sách các điểm dừng của tuyến đường
     */
    @Override
    public List<RouteStop> getRouteStops(UUID routeId) {
        if (!routeRepository.existsById(routeId)) {
            throw new NotFoundException("Route not found");
        }
        return routeStopRepository.findByRouteIdOrderByStopSequence(routeId);
    }

    /**
     * Lấy tất cả tuyến đường
     */
    @Override
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    /**
     * Lấy tuyến đường theo trạng thái
     */
    @Override
    public List<Route> getRoutesByStatus(Route.RouteStatus status) {
        return routeRepository.findByStatus(status);
    }

    /**
     * Lấy tuyến đường có đi qua hub
     */
    @Override
    public List<Route> getRoutesByHub(UUID hubId) {
        if (!hubRepo.existsById(hubId)) {
            throw new NotFoundException("Hub not found");
        }
        return routeRepository.findByHubId(hubId);
    }

    /**
     * Xóa tuyến đường
     */
    @Override
    @Transactional
    public void deleteRoute(UUID routeId) {
        if (!routeRepository.existsById(routeId)) {
            throw new NotFoundException("Route not found");
        }

        routeStopRepository.deleteByRouteId(routeId);
        routeRepository.deleteById(routeId);
    }

    /**
     * Cập nhật metrics của tuyến đường (khoảng cách, thời gian)
     */
    @Override
    public void updateRouteMetrics(UUID routeId) {
        List<RouteStop> stops = routeStopRepository.findByRouteIdOrderByStopSequence(routeId);

        if (stops.size() < 2) {
            return;
        }

        float totalDistance = 0.0f;
        int totalDuration = 0;

        List<Hub> hubs = stops.stream()
                .map(stop -> hubRepo.findById(stop.getHubId())
                        .orElseThrow(() -> new NotFoundException("Hub not found")))
                .toList();

        // Thêm thời gian chờ tại các hub
        for (RouteStop stop : stops) {
            if (stop.getEstimatedWaitTime() != null) {
                totalDuration += stop.getEstimatedWaitTime();
            }
        }

        // Tính khoảng cách giữa các điểm dừng liên tiếp
        for (int i = 0; i < hubs.size() - 1; i++) {
            Hub current = hubs.get(i);
            Hub next = hubs.get(i + 1);

            double distance = HaversineDistanceCalculator.calculateDistance(
                    current.getLatitude(), current.getLongitude(),
                    next.getLatitude(), next.getLongitude());

            totalDistance += distance;

            // Ước tính thời gian di chuyển (giả sử tốc độ trung bình 50 km/h)
            int travelMinutes = (int) (distance / 50.0 * 60);
            totalDuration += travelMinutes;
        }

        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new NotFoundException("Route not found"));

        route.setTotalDistance(totalDistance);
        route.setEstimatedDuration(totalDuration);

        routeRepository.save(route);
    }
}
