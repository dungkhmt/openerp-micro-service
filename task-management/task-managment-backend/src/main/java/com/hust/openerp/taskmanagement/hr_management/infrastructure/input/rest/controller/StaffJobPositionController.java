package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;

import jakarta.validation.Valid;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.BeanAwareUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffJobPositionModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.Resource;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.staff_job_position.request.GetJobPositionHistoryRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.staff_job_position.response.StaffJobPositionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/job/")
public class StaffJobPositionController extends BeanAwareUseCasePublisher {

    @PostMapping("get-job-position-history")
    public ResponseEntity<?> getJobPositionHistory(
            @Valid @RequestBody GetJobPositionHistoryRequest request
    ){
        var models = publishCollection(StaffJobPositionModel.class, request.toUseCase());
        var response = models.stream()
                .map(StaffJobPositionResponse::fromModel)
                .toList();
        return ResponseEntity.ok().body(
                new Resource(response)
        );
    }
}
