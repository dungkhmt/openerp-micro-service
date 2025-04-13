package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.holiday.usecase_data.DeleteHoliday;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.holiday.usecase_data.GetHoliday;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.holiday.usecase_data.GetHolidayList;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.BeanAwareUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.HolidayListModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.HolidayModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.Resource;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.holiday.request.CreateHolidayRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.holiday.request.UpdateHolidayRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.holiday.response.HolidayListResponse;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.holiday.response.HolidayResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/holidays")
public class HolidayController extends BeanAwareUseCasePublisher {

    @GetMapping("/{id}")
    public ResponseEntity<HolidayResponse> getHoliday(@PathVariable UUID id) {
        var useCase = new GetHoliday(id);
        var holidayModel = publish(HolidayModel.class, useCase);
        return ResponseEntity.ok(HolidayResponse.fromModel(holidayModel));
    }

    @GetMapping("/")
    public ResponseEntity<?> getHolidays(
        @RequestParam LocalDate startDate,
        @RequestParam LocalDate endDate) {

        var useCase = new GetHolidayList(startDate, endDate);
        var model = publish(HolidayListModel.class, useCase);

        return ResponseEntity.ok(HolidayListResponse.fromModel(model));
    }

    @PostMapping("/")
    public ResponseEntity<?> createHoliday(
            @Valid @RequestBody CreateHolidayRequest request
    ){
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @PutMapping("/")
    public ResponseEntity<?> updateHoliday(
            @Valid @RequestBody UpdateHolidayRequest request
    ){
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHoliday(
            @PathVariable UUID id
    ){
        var useCase = new DeleteHoliday(id);
        publish(useCase);
        return ResponseEntity.ok().body(
                new Resource()
        );
    }
}
