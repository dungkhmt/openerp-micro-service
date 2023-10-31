package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.StudyWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyWeekRepo extends JpaRepository<StudyWeek, Long> {
    List<StudyWeek> findAll();
}
