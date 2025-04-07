package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;

import jakarta.validation.Valid;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.BeanAwareUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.JobPositionModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.Resource;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.job_position.request.CreateJobPositionRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.job_position.request.DeleteJobPositionRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.job_position.request.GetJobPositionRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.job_position.request.UpdateJobPositionRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.job_position.response.JobPositionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("")
    public ResponseEntity<?> getJobPositions(
        @Valid @ModelAttribute GetJobPositionRequest request
    ){
        var modelPage = publishPageWrapper(JobPositionModel.class, request.toUseCase());
        var responsePage = modelPage.convert(JobPositionResponse::fromModel);
        return ResponseEntity.ok().body(
            new Resource(responsePage)
        );
    }
}
