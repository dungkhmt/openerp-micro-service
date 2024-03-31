package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.JobPost;
import openerp.openerpresourceserver.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface JobPostRepo  extends JpaRepository<JobPost, Integer> {
    public List<JobPost> findByUserId(String UserId);
}
