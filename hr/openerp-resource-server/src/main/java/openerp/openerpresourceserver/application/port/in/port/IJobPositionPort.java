package openerp.openerpresourceserver.application.port.in.port;

import openerp.openerpresourceserver.application.port.out.job_position.filter.IJobPositionFilter;
import openerp.openerpresourceserver.domain.model.IPageableRequest;
import openerp.openerpresourceserver.domain.model.JobPositionModel;
import openerp.openerpresourceserver.domain.model.PageWrapper;

import java.util.List;

public interface IJobPositionPort extends ICodeGeneratorPort {
    JobPositionModel findByCode(String code);
    List<JobPositionModel> findByCodeIn(List<String> code);
    void createJobPosition(JobPositionModel jobPosition);
    void updateJobPosition(JobPositionModel jobPosition);
    PageWrapper<JobPositionModel> getJobPosition(IJobPositionFilter filter, IPageableRequest request);
}
