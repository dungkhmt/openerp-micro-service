package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_job_position.usecase_data.StaffJobPositionHistory;
import jakarta.validation.Valid;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.BeanAwareUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffJobPositionModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.Resource;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.staff_job_position.request.GetJobPositionHistoryRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.staff_job_position.response.StaffJobPositionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/staffs/")
public class StaffJobPositionController extends BeanAwareUseCasePublisher {

    @GetMapping("{userId}/job-position")
    public ResponseEntity<?> getJobPositionHistory(
        @Valid @PathVariable String userId
    ){
        var models = publishCollection(StaffJobPositionModel.class, new StaffJobPositionHistory(userId));
        var response = models.stream()
                .map(StaffJobPositionResponse::fromModel)
                .toList();
        return ResponseEntity.ok().body(
                new Resource(response)
        );
    }
}
