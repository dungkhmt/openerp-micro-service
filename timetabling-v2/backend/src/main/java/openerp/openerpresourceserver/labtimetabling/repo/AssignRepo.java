package openerp.openerpresourceserver.labtimetabling.repo;

import openerp.openerpresourceserver.labtimetabling.entity.Assign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AssignRepo extends JpaRepository<Assign, UUID> {
    List<Assign> findAssignsBySemester_id(Long semId);

    @Query(value="SELECT DISTINCT semester FROM assigned", nativeQuery = true)
    List<String> findDistinctSemesters();
}
