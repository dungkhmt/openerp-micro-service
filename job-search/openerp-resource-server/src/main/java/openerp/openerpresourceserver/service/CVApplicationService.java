package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.CVApplication;
import openerp.openerpresourceserver.entity.JobPost;

import java.util.List;

public interface CVApplicationService {
    List<CVApplication> getAll();
    List<CVApplication> getAllByJobId(JobPost jobPost);

    CVApplication save(CVApplication cvApplication);
    void DeleteById(Integer id);
}
