package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassroomRepo extends JpaRepository<Classroom, Long> {
    List<Classroom> findAll();
}
