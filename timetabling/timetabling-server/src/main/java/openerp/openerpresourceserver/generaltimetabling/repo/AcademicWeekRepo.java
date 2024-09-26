package openerp.openerpresourceserver.generaltimetabling.repo;

import openerp.openerpresourceserver.generaltimetabling.model.entity.AcademicWeek;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcademicWeekRepo extends JpaRepository<AcademicWeek, Long> {

    List<AcademicWeek> findAllBySemester(String semester);

    void deleteBySemester(String semester);

    boolean findBySemester(String semester);
}
