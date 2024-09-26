package thesisdefensejuryassignment.thesisdefenseserver.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thesisdefensejuryassignment.thesisdefenseserver.entity.AcademicKeyword;
@Repository
public interface AcademicKeywordRepo extends JpaRepository<AcademicKeyword, String> {
}
