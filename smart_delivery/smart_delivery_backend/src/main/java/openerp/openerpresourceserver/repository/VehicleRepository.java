package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID>, VehicleCustomRepository {
}
