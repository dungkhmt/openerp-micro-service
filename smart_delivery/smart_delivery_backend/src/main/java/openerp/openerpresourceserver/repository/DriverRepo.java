package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface DriverRepo extends JpaRepository<Driver, UUID> {
    @Query("SELECT d FROM Driver d")
    List<Driver> getAllByHubId(UUID hubId);

    Driver findByUsername(String username);
}
