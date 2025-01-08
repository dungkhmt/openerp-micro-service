package openerp.openerpresourceserver.generaltimetabling.repo;

import openerp.openerpresourceserver.generaltimetabling.model.entity.Institute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstituteRepo extends JpaRepository<Institute, Long> {
    @Query(value = "SELECT DISTINCT institute FROM public.timetabling_schedule", nativeQuery = true)
    List<String> getInstitute();
}
