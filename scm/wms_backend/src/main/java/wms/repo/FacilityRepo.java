package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.Facility;

public interface FacilityRepo extends JpaRepository<Facility, Long> {
    @Query(value = "select * from facility", nativeQuery = true)
    Page<Facility> search(Pageable pageable);
    Facility getFacilityById(long id);
    Facility getFacilityByCode(String code);
}
