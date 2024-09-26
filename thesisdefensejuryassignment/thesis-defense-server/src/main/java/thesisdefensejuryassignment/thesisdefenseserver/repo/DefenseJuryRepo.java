package thesisdefensejuryassignment.thesisdefenseserver.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import thesisdefensejuryassignment.thesisdefenseserver.entity.DefenseJury;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DefenseJuryRepo extends JpaRepository<DefenseJury, UUID> {
    @Override
    @Query(value = "select * from defense_jury d where d.id= :Id", nativeQuery = true)
    Optional<DefenseJury> findById(UUID Id);
}
