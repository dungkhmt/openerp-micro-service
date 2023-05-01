package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.TruckEntity;

public interface TruckRepo extends JpaRepository<TruckEntity, Long> {
    @Query(value = "select * from scm_truck", nativeQuery = true)
    Page<TruckEntity> search(Pageable pageable);
    TruckEntity getTruckById(long id);
    TruckEntity getTruckByCode(String code);
}
