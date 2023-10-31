package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.StudyTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyTimeRepo extends JpaRepository<StudyTime, Long> {
}
