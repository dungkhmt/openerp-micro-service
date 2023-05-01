package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.DroneEntity;
import wms.entity.TruckEntity;

public interface DroneRepo extends JpaRepository<DroneEntity, Long> {
    @Query(value = "select * from scm_drone", nativeQuery = true)
    Page<DroneEntity> search(Pageable pageable);
    DroneEntity getDroneById(long id);
    DroneEntity getDroneByCode(String code);
}
