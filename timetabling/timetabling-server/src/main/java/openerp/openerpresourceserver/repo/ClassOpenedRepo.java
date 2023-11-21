package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.ClassOpened;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassOpenedRepo extends JpaRepository<ClassOpened, Long> {

    void deleteById(Long id);

    List<ClassOpened> findAll(Sort sort);

    List<ClassOpened> getAllByIdIn(List<Long> ids, Sort sort);

    List<ClassOpened> getAllBySemester(String semester, Sort sort);
}
