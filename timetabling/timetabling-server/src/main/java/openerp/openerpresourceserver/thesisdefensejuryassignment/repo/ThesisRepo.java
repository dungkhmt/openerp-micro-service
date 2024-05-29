package openerp.openerpresourceserver.thesisdefensejuryassignment.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.DefenseJury;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.Thesis;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ThesisRepo extends JpaRepository<Thesis, UUID>{
//    @Query(value = "select * from thesis d where d.scheduled_jury_id is null and d.thesis_defense_plan_id = :thesisDefensePlanId", nativeQuery = true)
    public Optional<List<Thesis>> findByThesisDefensePlanIdAndDefenseJury(String thesisDefensePlanId, UUID scheduledJuryId);

    @Override
    @Query(value = "select * from thesis d where d.id= :Id", nativeQuery = true)
    Optional<Thesis> findById(UUID Id);

    public Optional<List<Thesis>> findByStudentEmail(String studentEmail);
}