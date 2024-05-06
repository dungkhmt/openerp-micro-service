package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.CodePlagiarism;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CodePlagiarismRepo extends JpaRepository<CodePlagiarism, String> {

}
