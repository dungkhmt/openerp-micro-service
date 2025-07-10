package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.entity.RouteStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RouteStopRepository extends JpaRepository<RouteStop, UUID> {
    List<RouteStop> findByRouteIdOrderByStopSequence(UUID routeId);

    void deleteByRouteId(UUID routeId);
}