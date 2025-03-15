package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;

import jakarta.validation.Valid;
import openerp.openerpresourceserver.domain.common.usecase.BeanAwareUseCasePublisher;
import openerp.openerpresourceserver.domain.model.StaffDetailModel;
import openerp.openerpresourceserver.domain.model.StaffModel;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.common.response.resource.Resource;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.staff.request.*;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.staff.response.StaffDetailResponse;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.staff.response.StaffResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/staff/")
public class  StaffController extends BeanAwareUseCasePublisher {
    @PostMapping("add-staff")
    public ResponseEntity<?> addStaff(
            @Valid @RequestBody AddStaffRequest request
    ){
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @PostMapping("delete-staff")
    public ResponseEntity<?> deleteStaff(
            @Valid @RequestBody DeleteStaffRequest request
    ){
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @PostMapping("edit-staff")
    public ResponseEntity<?> editStaff(
            @Valid @RequestBody EditStaffRequest request
    ){
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @PostMapping("search-staff")
    public ResponseEntity<?> searchStaff(
            @Valid @RequestBody SearchStaffRequest request
    ){
        var staffPage = publishPageWrapper(StaffModel.class, request.toUseCase());
        var responsePage = staffPage.convert(StaffResponse::fromModel);
        return ResponseEntity.ok().body(
                new Resource(responsePage)
        );
    }

    @PostMapping("get-all-staff-info")
    public ResponseEntity<?> getAllStaffInfo(
            @Valid @RequestBody GetAllStaffInfoRequest request
    ){
        var staffPage = publishPageWrapper(StaffDetailModel.class, request.toUseCase());
        var responsePage = staffPage.convert(StaffDetailResponse::fromModel);
        return ResponseEntity.ok().body(
                new Resource(responsePage)
        );
    }

    @PostMapping("get-staff-info")
    public ResponseEntity<?> getStaffInfo(
            @Valid @RequestBody GetStaffInfoRequest request
    ){
        var staff = publish(StaffDetailModel.class, request.toUseCase());
        return ResponseEntity.ok().body(
                new Resource(StaffDetailResponse.fromModel(staff))
        );
    }
}
