package openerp.openerpresourceserver.service;
import openerp.openerpresourceserver.entity.JobPost;

import java.util.List;
public interface JobPostService {
    List<JobPost> getAll();
    List<JobPost> getAllByUserId(String id);
    JobPost getById(Integer id);
    JobPost save(JobPost jobPost);
    void deleteById(Integer id);
}
