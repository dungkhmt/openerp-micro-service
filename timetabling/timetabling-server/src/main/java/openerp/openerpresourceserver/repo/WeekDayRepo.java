package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.WeekDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeekDayRepo extends JpaRepository<WeekDay, Long> {
}
