package openerp.openerpresourceserver.labtimetabling.repo;

import openerp.openerpresourceserver.labtimetabling.entity.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClassRepo extends JpaRepository<Class, Long> {
    @Query(value="SELECT DISTINCT semester FROM class", nativeQuery = true)
    List<String> findDistinctSemesters();

    List<Class> findAllBySemester_id(Long semId);
}
