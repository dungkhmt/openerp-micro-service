package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.adapter;

import lombok.RequiredArgsConstructor;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IJobPositionPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.job_position.filter.IJobPositionFilter;
import com.hust.openerp.taskmanagement.hr_management.domain.exception.ApplicationException;
import com.hust.openerp.taskmanagement.hr_management.domain.model.JobPositionModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.IPageableRequest;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PageWrapper;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.ResponseCode;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.JobPositionEntity;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository.JobPositionRepo;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.specification.JobPositionSpecification;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.utils.PageableUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class JobPositionAdapter implements IJobPositionPort {
    private final JobPositionRepo jobPositionRepo;

    @Override
    public JobPositionModel findByCode(String code) {
        return toModel(
                jobPositionRepo.findById(code).orElseThrow(
                        () -> new ApplicationException(
                                ResponseCode.JOB_POSITION_NOT_EXISTED,
                                String.format("JobPosition not existed by code: %s", code)
                        )
                )
        );
    }

    @Override
    public List<JobPositionModel> findByCodeIn(List<String> codes) {
        return toModels(jobPositionRepo.findByPositionCodeIn(codes));
    }

    @Override
    public void createJobPosition(JobPositionModel jobPosition) {
        validateCreateJobPosition(jobPosition);
        var jobPositionEntity = new JobPositionEntity();
        jobPositionEntity.setPositionCode(jobPosition.getCode());
        jobPositionEntity.setPositionName(jobPosition.getName());
        jobPositionEntity.setDescription(jobPosition.getDescription());
        jobPositionEntity.setStatus(jobPosition.getStatus());
        jobPositionEntity.setType(jobPosition.getType());
        jobPositionRepo.save(jobPositionEntity);
    }


    @Override
    public void updateJobPosition(JobPositionModel jobPosition) {

        var jobPositionEntity = jobPositionRepo.findById(jobPosition.getCode())
                .orElseThrow(() -> new ApplicationException(
                                ResponseCode.JOB_POSITION_NOT_EXISTED,
                                String.format("JobPosition not exist with code: %s", jobPosition.getCode())
                        )
                );

        if(jobPosition.getName() != null){
            if (!jobPositionEntity.getPositionName().equals(jobPosition.getName())) {
                validateJobPositionName(jobPosition.getName());
                jobPositionEntity.setPositionName(jobPosition.getName());
            }
        }
        if(jobPosition.getDescription() != null){
            jobPositionEntity.setDescription(jobPosition.getDescription());
        }

        if(jobPosition.getType() != null){
            jobPositionEntity.setType(jobPosition.getType());
        }

        if(jobPosition.getStatus() != null){
            jobPositionEntity.setStatus(jobPosition.getStatus());
        }
        jobPositionRepo.save(jobPositionEntity);
    }

    @Override
    public List<JobPositionModel> getJobPosition(IJobPositionFilter filter) {
        var spec = new JobPositionSpecification(filter);
        return toModels(jobPositionRepo.findAll(spec));
    }

    @Override
    public PageWrapper<JobPositionModel> getJobPosition(IJobPositionFilter filter, IPageableRequest request) {
        var pageable = PageableUtils.getPageable(request);
        var spec = new JobPositionSpecification(filter);
        var page = jobPositionRepo.findAll(spec ,pageable);
        return PageWrapper.<JobPositionModel>builder()
                .pageInfo(PageableUtils.getPageInfo(page))
                .pageContent(toModels(page.getContent()))
                .build();
    }


    private JobPositionModel toModel(JobPositionEntity jobPosition) {
        return JobPositionModel.builder()
            .code(jobPosition.getPositionCode())
            .name(jobPosition.getPositionName())
            .description(jobPosition.getDescription())
            .type(jobPosition.getType())
            .status(jobPosition.getStatus())
            .build();
    }

    private List<JobPositionModel> toModels(Collection<JobPositionEntity> departments) {
        return departments.stream()
                .map(this::toModel)
                .toList();
    }

    private void validateCreateJobPosition(JobPositionModel jobPosition) {
        if(jobPositionRepo.existsById(jobPosition.getCode())){
            throw new ApplicationException(
                    ResponseCode.JOB_POSITION_CODE_EXISTED,
                    String.format("JobPosition code already exists: %s", jobPosition.getCode())
            );
        }
        validateJobPositionName(jobPosition.getName());
    }

    private void validateJobPositionName(String name) {
        if(jobPositionRepo.existsByPositionName(name)){
            throw new ApplicationException(
                    ResponseCode.JOB_POSITION_NAME_EXISTED,
                    String.format("JobPosition name already exists: %s", name)
            );
        }
    }

    @Override
    public String findMaxCode(String prefix) {
        return jobPositionRepo.findMaxJobCode(prefix);
    }
}
