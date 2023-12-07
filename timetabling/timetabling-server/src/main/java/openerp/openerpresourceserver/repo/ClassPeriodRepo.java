package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.ClassPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassPeriodRepo extends JpaRepository<ClassPeriod, Long> {

    void deleteById(Long id);
}
