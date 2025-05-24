package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_department.usecase_data.StaffDepartmentHistory;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.BeanAwareUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffDepartmentModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.Resource;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.staff_department.response.StaffDepartmentResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/staffs/")
public class StaffDepartmentController extends BeanAwareUseCasePublisher {

    @GetMapping("{userId}/department")
    public ResponseEntity<?> getDepartmentHistory(
            @Valid @PathVariable String userId
    ){
        var models = publishCollection(StaffDepartmentModel.class, new StaffDepartmentHistory(userId));
        var response = models.stream()
                .map(StaffDepartmentResponse::fromModel)
                .toList();
        return ResponseEntity.ok().body(
                new Resource(response)
        );
    }
}
