package openerp.openerpresourceserver.generaltimetabling.repo;

import openerp.openerpresourceserver.generaltimetabling.model.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SemesterRepo extends JpaRepository<Semester, Long> {
    @Query(value = "SELECT DISTINCT semester FROM public.timetabling_schedule", nativeQuery = true)
    List<String> getSemester();

    void deleteById(Long id);

    List<Semester> getSemestersBySemester(String semester);
}
