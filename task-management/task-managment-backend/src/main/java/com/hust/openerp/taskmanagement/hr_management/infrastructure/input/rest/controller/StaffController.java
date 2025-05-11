package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.usecase_data.EditStaff;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.usecase_data.GetStaffInfo;
import jakarta.validation.Valid;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.BeanAwareUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffDetailModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.Resource;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.staff.request.*;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.staff.response.StaffDetailResponse;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.staff.response.StaffResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/staffs")
public class  StaffController extends BeanAwareUseCasePublisher {

    @GetMapping("/me")
    public ResponseEntity<?> getStaff(
        Principal principal
    ){
        var useCase = GetStaffInfo.builder().userLoginId(principal.getName()).build();
        var staff = publish(StaffDetailModel.class, useCase);
        return ResponseEntity.ok().body(
            new Resource(StaffDetailResponse.fromModel(staff))
        );
    }

    @PutMapping("")
    public ResponseEntity<?> addStaff(
            @Valid @RequestBody AddStaffRequest request
    ){
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @DeleteMapping("/{staffCode}")
    public ResponseEntity<?> deleteStaff(
        @PathVariable String staffCode,
            @Valid @RequestBody DeleteStaffRequest request
    ){
        publish(EditStaff.delete(staffCode));
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @PostMapping("/{staffCode}")
    public ResponseEntity<?> editStaff(
        @PathVariable String staffCode,
            @Valid @RequestBody EditStaffRequest request
    ){
        publish(request.toUseCase(staffCode));
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @GetMapping("")
    public ResponseEntity<?> searchStaff(
            @Valid @RequestBody SearchStaffRequest request
    ){
        var staffPage = publishPageWrapper(StaffModel.class, request.toUseCase());
        var responsePage = staffPage.convert(StaffResponse::fromModel);
        return ResponseEntity.ok().body(
                new Resource(responsePage)
        );
    }

    @PostMapping("/details")
    public ResponseEntity<?> getAllStaffInfo(
            @Valid @RequestBody GetAllStaffInfoRequest request
    ){
        var staffPage = publishPageWrapper(StaffDetailModel.class, request.toUseCase());
        var responsePage = staffPage.convert(StaffDetailResponse::fromModel);
        return ResponseEntity.ok().body(
                new Resource(responsePage)
        );
    }

    @GetMapping("/{staffCode}")
    public ResponseEntity<?> getStaffInfo(
        @PathVariable String staffCode
    ){
        var staff = publish(StaffDetailModel.class, GetStaffInfo.builder()
            .staffCode(staffCode)
            .build());
        return ResponseEntity.ok().body(
                new Resource(StaffDetailResponse.fromModel(staff))
        );
    }
}
