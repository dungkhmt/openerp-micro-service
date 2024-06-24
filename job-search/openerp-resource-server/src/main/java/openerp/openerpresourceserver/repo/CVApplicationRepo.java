package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.CVApplication;
import openerp.openerpresourceserver.entity.JobPost;
import openerp.openerpresourceserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CVApplicationRepo extends JpaRepository<CVApplication, Integer> {
    public List<CVApplication> findByJobId(JobPost jobPost);
    public List<CVApplication> findByUserId(String user);
}
