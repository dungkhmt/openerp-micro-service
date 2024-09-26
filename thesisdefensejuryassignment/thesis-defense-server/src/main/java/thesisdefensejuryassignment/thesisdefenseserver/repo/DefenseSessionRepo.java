package thesisdefensejuryassignment.thesisdefenseserver.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thesisdefensejuryassignment.thesisdefenseserver.entity.DefenseSession;

@Repository
public interface DefenseSessionRepo extends JpaRepository<DefenseSession, Integer> {
}
