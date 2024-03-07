package thesisdefensejuryassignment.thesisdefenseserver.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thesisdefensejuryassignment.thesisdefenseserver.entity.DefenseJury;

import java.util.UUID;

@Repository
public interface DefenseJuryRepo extends JpaRepository<DefenseJury, UUID> {
}
