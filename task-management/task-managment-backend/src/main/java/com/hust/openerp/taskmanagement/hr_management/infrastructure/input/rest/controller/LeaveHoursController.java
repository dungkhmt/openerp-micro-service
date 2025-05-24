package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;

import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.BeanAwareUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.Resource;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.leave_hours.request.UpdateLeaveHoursRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/leave_hours")
public class LeaveHoursController extends BeanAwareUseCasePublisher {

    @PatchMapping()
    public ResponseEntity<?> updateLeaveHours(
        @Valid @RequestBody UpdateLeaveHoursRequest request
    ){
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
            new Resource()
        );
    }
}
