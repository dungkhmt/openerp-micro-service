package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.Facility;

import java.util.List;

public interface FacilityRepo extends JpaRepository<Facility, Long> {
    @Query(value = "select * from scm_facility", nativeQuery = true)
    Page<Facility> search(Pageable pageable);
    Facility getFacilityById(long id);
    Facility getFacilityByCode(String code);

    @Query(value = "select * from scm_facility", nativeQuery = true)
    List<Facility> getAllFacility();
}
