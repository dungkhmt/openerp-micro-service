package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.ClassCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassCodeRepo extends JpaRepository<ClassCode, Long> {
    List<ClassCode> findAll();
}
