package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.usecase_data.GetCheckpointPeriodDetails;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.usecase_data.UpdateCheckpointPeriod;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.BeanAwareUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointPeriodDetailsModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointPeriodModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_period.request.CreateCheckpointPeriodRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_period.request.GetAllCheckpointPeriodRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_period.request.UpdateCheckpointPeriodRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_period.response.CheckpointPeriodDetailsResponse;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_period.response.CheckpointPeriodResponse;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.Resource;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/checkpoints/periods")
public class CheckpointPeriodController extends BeanAwareUseCasePublisher {
    @PostMapping("")
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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCheckpointPeriod(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateCheckpointPeriodRequest request
    ){
        publish(request.toUseCase(id));
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCheckpointPeriod(
        @PathVariable UUID id
    ){
        publish(UpdateCheckpointPeriod.delete(id));
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @GetMapping("")
    public ResponseEntity<?> getCheckpointPeriod(
            @Valid @ModelAttribute GetAllCheckpointPeriodRequest request
    ){
        var modelPage = publishPageWrapper(CheckpointPeriodModel.class, request.toUseCase());
        var responsePage = modelPage.convert(CheckpointPeriodResponse::fromModel);
        return ResponseEntity.ok().body(
                new Resource(responsePage)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCheckpointPeriodDetail(
            @PathVariable UUID id
    ){
        var model = publish(CheckpointPeriodDetailsModel.class, new GetCheckpointPeriodDetails(id));
        var response = CheckpointPeriodDetailsResponse.fromModel(model);
        return ResponseEntity.ok().body(
                new Resource(response)
        );
    }
}
