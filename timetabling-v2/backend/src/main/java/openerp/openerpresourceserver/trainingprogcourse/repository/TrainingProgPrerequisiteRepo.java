package openerp.openerpresourceserver.trainingprogcourse.repository;

import openerp.openerpresourceserver.trainingprogcourse.enity.TrainingProgPrerequisite;
import openerp.openerpresourceserver.trainingprogcourse.enity.TrainingProgPrerequisiteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingProgPrerequisiteRepo extends JpaRepository<TrainingProgPrerequisite, TrainingProgPrerequisiteId> {
}
