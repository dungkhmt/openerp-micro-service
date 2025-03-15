package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;

import jakarta.validation.Valid;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.BeanAwareUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointPeriodDetailsModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointPeriodModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_period.request.*;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_period.response.CheckpointPeriodDetailsResponse;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_period.response.CheckpointPeriodResponse;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/checkpoint/")
public class CheckpointPeriodController extends BeanAwareUseCasePublisher {
    @PostMapping("create-period")
    public ResponseEntity<?> createCheckpointPeriod(
            Principal principal,
            @Valid @RequestBody CreateCheckpointPeriodRequest request
    ){
        request.setCreatedByUserId(principal.getName());
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @PostMapping("update-period")
    public ResponseEntity<?> updateCheckpointPeriod(
            @Valid @RequestBody UpdateCheckpointPeriodRequest request
    ){
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @PostMapping("delete-period")
    public ResponseEntity<?> deleteCheckpointPeriod(
            @Valid @RequestBody DeleteCheckpointPeriodRequest request
    ){
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @PostMapping("get-all-period")
    public ResponseEntity<?> getCheckpointPeriod(
            @Valid @RequestBody GetAllCheckpointPeriodRequest request
    ){
        var modelPage = publishPageWrapper(CheckpointPeriodModel.class, request.toUseCase());
        var responsePage = modelPage.convert(CheckpointPeriodResponse::fromModel);
        return ResponseEntity.ok().body(
                new Resource(responsePage)
        );
    }

    @PostMapping("get-period-detail")
    public ResponseEntity<?> getCheckpointPeriodDetail(
            @Valid @RequestBody GetCheckpointPeriodDetailsRequest request
    ){
        var model = publish(CheckpointPeriodDetailsModel.class, request.toUseCase());
        var response = CheckpointPeriodDetailsResponse.fromModel(model);
        return ResponseEntity.ok().body(
                new Resource(response)
        );
    }
}
