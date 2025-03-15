package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;

import jakarta.validation.Valid;
import openerp.openerpresourceserver.domain.common.usecase.BeanAwareUseCasePublisher;
import openerp.openerpresourceserver.domain.model.StaffDepartmentModel;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.common.response.resource.Resource;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.staff_department.request.GetDepartmentHistoryRequest;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.staff_department.response.StaffDepartmentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/department/")
public class StaffDepartmentController extends BeanAwareUseCasePublisher {

    @PostMapping("get-department-history")
    public ResponseEntity<?> getDepartmentHistory(
            @Valid @RequestBody GetDepartmentHistoryRequest request
    ){
        var models = publishCollection(StaffDepartmentModel.class, request.toUseCase());
        var response = models.stream()
                .map(StaffDepartmentResponse::fromModel)
                .toList();
        return ResponseEntity.ok().body(
                new Resource(response)
        );
    }
}
