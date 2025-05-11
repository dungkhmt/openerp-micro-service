package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.job_position.usecase_data.UpdateJobPosition;
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
@RequestMapping("/jobs")
public class JobPositionController extends BeanAwareUseCasePublisher {
    @PostMapping("/")
    public ResponseEntity<?> createJobPosition(
            @Valid @RequestBody CreateJobPositionRequest request
    ){
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @PutMapping("/{code}")
    public ResponseEntity<?> updateJobPosition(
        @PathVariable String code,
        @Valid @RequestBody UpdateJobPositionRequest request
    ){
        publish(request.toUseCase(code));
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<?> deleteJobPosition(
        @PathVariable String code
    ){
        publish(UpdateJobPosition.delete(code));
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @GetMapping("/")
    public ResponseEntity<?> getJobPosition(
            @Valid @RequestBody GetJobPositionRequest request
    ){
        var modelPage = publishPageWrapper(JobPositionModel.class, request.toUseCase());
        var responsePage = modelPage.convert(JobPositionResponse::fromModel);
        return ResponseEntity.ok().body(
                new Resource(responsePage)
        );
    }

    @GetMapping("/")
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
