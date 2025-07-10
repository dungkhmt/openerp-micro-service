package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RouteRepository extends JpaRepository<Route, UUID> {
    Optional<Route> findByRouteCode(String routeCode);

    @Query("SELECT r FROM Route r WHERE r.status = :status")
    List<Route> findByStatus(@Param("status") Route.RouteStatus status);

    @Query("SELECT DISTINCT r FROM Route r JOIN RouteStop rs ON r.routeId = rs.routeId WHERE rs.hubId = :hubId")
    List<Route> findByHubId(@Param("hubId") UUID hubId);
}