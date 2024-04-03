package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.CVApplication;
import openerp.openerpresourceserver.entity.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CVApplicationRepo extends JpaRepository<CVApplication, Integer> {
    public List<CVApplication> findByJobId(JobPost jobPost);
}
