package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.ClassOpened;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassOpenedRepo extends JpaRepository<ClassOpened, Long> {

    void deleteById(Long id);

    List<ClassOpened> findAll();
}
