package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.CVApplication;
import openerp.openerpresourceserver.entity.JobPost;
import openerp.openerpresourceserver.entity.User;

import java.util.List;

public interface CVApplicationService {
    List<CVApplication> getAll();
    List<CVApplication> getAllByJobId(JobPost jobPost);
    List<CVApplication> getAllByUserId(String userId);
    CVApplication save(CVApplication cvApplication);
    void DeleteById(Integer id);
}
