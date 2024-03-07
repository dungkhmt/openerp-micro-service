package thesisdefensejuryassignment.thesisdefenseserver.repo;

import thesisdefensejuryassignment.thesisdefenseserver.entity.ThesisDefensePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThesisDefensePlanRepo extends JpaRepository<ThesisDefensePlan, String> {

    public Optional<ThesisDefensePlan> findByName(String name);

    public Optional<ThesisDefensePlan> findByNameAndAndId(String name, String id);

}
