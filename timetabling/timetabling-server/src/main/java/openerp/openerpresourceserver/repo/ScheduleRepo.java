package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepo extends JpaRepository<Schedule, Long> {
}
