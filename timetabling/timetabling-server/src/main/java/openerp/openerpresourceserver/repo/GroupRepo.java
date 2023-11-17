package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepo extends JpaRepository<Group, Long> {
    @Query(value = "SELECT DISTINCT semester FROM public.timetabling_group", nativeQuery = true)
    List<String> getSemester();
}
