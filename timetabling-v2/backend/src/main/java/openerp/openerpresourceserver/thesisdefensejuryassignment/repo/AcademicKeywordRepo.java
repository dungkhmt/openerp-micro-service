package openerp.openerpresourceserver.thesisdefensejuryassignment.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.AcademicKeyword;
@Repository
public interface AcademicKeywordRepo extends JpaRepository<AcademicKeyword, String> {
}
