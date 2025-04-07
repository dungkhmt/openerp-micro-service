package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;

import jakarta.validation.Valid;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.usecase_data.Checkinout;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.BeanAwareUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.AttendanceModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckinoutModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkinout.request.GetCheckinoutRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkinout.request.MonthAttendanceRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkinout.response.GetCheckinoutResponse;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkinout.response.MonthAttendanceResponse;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/")
public class CheckinoutController extends BeanAwareUseCasePublisher {

    @PostMapping("checkinout")
    public ResponseEntity<?> checkinout(Principal principal){
        publish(Checkinout.from(principal.getName()));
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @PostMapping("get-checkinout")
    public ResponseEntity<?> getCheckinout(
            @Valid @RequestBody GetCheckinoutRequest request,
            Principal principal
    ){
        var models = publishCollection(CheckinoutModel.class, request.toUseCase(principal.getName()));
        return ResponseEntity.ok().body(
                new Resource(models.stream().map(GetCheckinoutResponse::fromModel).toList())
        );
    }

    @PostMapping("month-attendance")
    public ResponseEntity<?> getMonthAttendance(
            @Valid @RequestBody MonthAttendanceRequest request
    ){
        var models = publishCollection(AttendanceModel.class, request.toUseCase());
        return ResponseEntity.ok().body(
                new Resource(MonthAttendanceResponse.fromModels(models))
        );
    }
}
