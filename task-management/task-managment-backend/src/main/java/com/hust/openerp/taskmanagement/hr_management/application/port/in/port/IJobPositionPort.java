package com.hust.openerp.taskmanagement.hr_management.application.port.in.port;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.job_position.filter.IJobPositionFilter;
import com.hust.openerp.taskmanagement.hr_management.domain.model.IPageableRequest;
import com.hust.openerp.taskmanagement.hr_management.domain.model.JobPositionModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PageWrapper;

import java.util.List;

public interface IJobPositionPort extends ICodeGeneratorPort {
    JobPositionModel findByCode(String code);
    List<JobPositionModel> findByCodeIn(List<String> code);
    void createJobPosition(JobPositionModel jobPosition);
    void updateJobPosition(JobPositionModel jobPosition);

    List<JobPositionModel> getJobPosition(IJobPositionFilter filter);

    PageWrapper<JobPositionModel> getJobPosition(IJobPositionFilter filter, IPageableRequest request);
}
