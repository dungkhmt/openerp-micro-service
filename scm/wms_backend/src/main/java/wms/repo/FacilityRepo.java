package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.Facility;

import java.util.List;

public interface FacilityRepo extends JpaRepository<Facility, Long> {
    @Query(value = "select * from scm_facility sf where sf.is_deleted = 0\n" +
            "and (sf.name ilike concat('%', :facilityName, '%'))\n" +
            "and (sf.status = :status or :status = '')\n" +
            "and (sf.managed_by = :managedBy or :managedBy = '')\n" +
            "and (sf.created_by = :createdBy or :createdBy = '')\n" +
            "and (sf.name ilike concat('%', :text, '%')\n" +
            "        or sf.status ilike concat('%', :text, '%')\n" +
            "        or sf.managed_by ilike concat('%', :text, '%')\n" +
            "        or sf.created_by ilike concat('%', :text, '%')\n" +
            "        )", nativeQuery = true)
    Page<Facility> search(Pageable pageable, String facilityName, String status, String createdBy, String managedBy, String text);
    Facility getFacilityById(long id);
    Facility getFacilityByCode(String code);

    @Query(value = "select * from scm_facility where is_deleted = 0", nativeQuery = true)
    List<Facility> getAllFacility();

    @Query(value = "SELECT *\n" +
            "FROM scm_facility\n" +
            "where EXTRACT(YEAR FROM created_date) = :year and\n" +
            "       EXTRACT(MONTH FROM created_date) = :month", nativeQuery = true)
    List<Facility> getFacilityCreatedMonthly(int month, int year);
}
