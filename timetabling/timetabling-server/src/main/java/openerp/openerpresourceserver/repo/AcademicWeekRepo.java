package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.AcademicWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcademicWeekRepo extends JpaRepository<AcademicWeek, Long> {

}
