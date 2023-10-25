package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SemesterRepo extends JpaRepository<Semester, Long> {
    List<Semester> findAll();
}
