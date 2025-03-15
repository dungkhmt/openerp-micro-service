package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;

import jakarta.validation.Valid;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.BeanAwareUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.DepartmentModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.Resource;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.department.request.CreateDepartmentRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.department.request.DeleteDepartmentRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.department.request.GetDepartmentRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.department.request.UpdateDepartmentRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.department.response.DepartmentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/department/")
public class DepartmentController extends BeanAwareUseCasePublisher {
    @PostMapping("create-department")
    public ResponseEntity<?> createDepartment(
            @Valid @RequestBody CreateDepartmentRequest request
    ){
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @PostMapping("update-department")
    public ResponseEntity<?> updateDepartment(
            @Valid @RequestBody UpdateDepartmentRequest request
    ){
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @PostMapping("delete-department")
    public ResponseEntity<?> deleteDepartment(
            @Valid @RequestBody DeleteDepartmentRequest request
    ){
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @PostMapping("get-department")
    public ResponseEntity<?> getDepartment(
            @Valid @RequestBody GetDepartmentRequest request
    ){
        var modelPage = publishPageWrapper(DepartmentModel.class, request.toUseCase());
        var responsePage = modelPage.convert(DepartmentResponse::fromModel);
        return ResponseEntity.ok().body(
                new Resource(responsePage)
        );
    }
}
