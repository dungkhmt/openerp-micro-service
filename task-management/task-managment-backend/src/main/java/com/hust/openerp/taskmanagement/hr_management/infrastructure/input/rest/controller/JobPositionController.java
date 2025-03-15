package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;

import jakarta.validation.Valid;
import openerp.openerpresourceserver.domain.common.usecase.BeanAwareUseCasePublisher;
import openerp.openerpresourceserver.domain.model.JobPositionModel;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.common.response.resource.Resource;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.job_position.request.CreateJobPositionRequest;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.job_position.request.DeleteJobPositionRequest;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.job_position.request.GetJobPositionRequest;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.job_position.request.UpdateJobPositionRequest;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.job_position.response.JobPositionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/job/")
public class JobPositionController extends BeanAwareUseCasePublisher {
    @PostMapping("create-job-position")
    public ResponseEntity<?> createJobPosition(
            @Valid @RequestBody CreateJobPositionRequest request
    ){
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @PostMapping("update-job-position")
    public ResponseEntity<?> updateJobPosition(
            @Valid @RequestBody UpdateJobPositionRequest request
    ){
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @PostMapping("delete-job-position")
    public ResponseEntity<?> deleteJobPosition(
            @Valid @RequestBody DeleteJobPositionRequest request
    ){
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @PostMapping("get-job-position")
    public ResponseEntity<?> getJobPosition(
            @Valid @RequestBody GetJobPositionRequest request
    ){
        var modelPage = publishPageWrapper(JobPositionModel.class, request.toUseCase());
        var responsePage = modelPage.convert(JobPositionResponse::fromModel);
        return ResponseEntity.ok().body(
                new Resource(responsePage)
        );
    }
}
