package openerp.openerpresourceserver.thesisdefensejuryassignment.repo;

import org.springframework.data.jpa.repository.Query;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.ThesisDefensePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThesisDefensePlanRepo extends JpaRepository<ThesisDefensePlan, String> {

    public Optional<ThesisDefensePlan> findByName(String name);

    public Optional<ThesisDefensePlan> findByNameAndAndId(String name, String id);

    @Query(value = "select *" +
            "from thesis_defense_plan tdp " +
            "inner join defense_jury dj "+
            "on tdp.id  = dj.thesis_defense_plan_id "+
            "inner join defense_jury_teacher djt " +
            "on dj.id = djt.jury_id "+
            "where djt.teacher_id = :teacherId", nativeQuery = true)
    public Optional<List<ThesisDefensePlan>> findByAssignedTeacherId(String teacherId);
}
