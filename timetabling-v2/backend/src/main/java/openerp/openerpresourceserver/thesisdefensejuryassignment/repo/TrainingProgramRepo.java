package openerp.openerpresourceserver.thesisdefensejuryassignment.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.DefenseJury;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.TrainingProgram;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrainingProgramRepo extends JpaRepository<TrainingProgram, UUID> {
    @Override
    @Query(value = "select * from training_program d where d.id= :Id", nativeQuery = true)
    Optional<TrainingProgram> findById(UUID Id);

}
