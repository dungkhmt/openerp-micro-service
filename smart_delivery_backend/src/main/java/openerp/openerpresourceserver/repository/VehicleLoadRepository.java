package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.entity.VehicleLoad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleLoadRepository extends JpaRepository<VehicleLoad, UUID> {

    /**
     * Find vehicle load by vehicle ID
     */
    Optional<VehicleLoad> findByVehicleId(UUID vehicleId);
}