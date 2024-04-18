package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.AcademicWeek;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcademicWeekRepo extends JpaRepository<AcademicWeek, Long> {

    List<AcademicWeek> findAllBySemester(String semester);

    void deleteBySemester(String semester);
}
