package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.roster_template.usecase_data.DeleteRosterTemplate;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.roster_template.usecase_data.GetRosterTemplateList;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.BeanAwareUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.RosterTemplateModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.Resource;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.roster_template.request.CreateRosterTemplatesRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.roster_template.request.UpdateFilterRosterRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.roster_template.request.UpdateRosterTemplateRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.roster_template.response.RosterTemplateResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/roster-templates")
public class RosterTemplateController extends BeanAwareUseCasePublisher {

    @PostMapping
    public ResponseEntity<?> createRosterTemplatesRequest(
        @Valid @RequestBody CreateRosterTemplatesRequest request
    ){
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
            new Resource()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRosterTemplate(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateRosterTemplateRequest request
    ){
        publish(request.toUseCase(id));
        return ResponseEntity.ok().body(
            new Resource()
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateRosterTemplate(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateFilterRosterRequest request
    ){
        publish(request.toUseCase(id));
        return ResponseEntity.ok().body(
            new Resource()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelRosterTemplate(
        @PathVariable UUID id
    ){
        var useCase = new DeleteRosterTemplate(id);
        publish(useCase);
        return ResponseEntity.ok().body(
            new Resource()
        );
    }

    @GetMapping("")
    public ResponseEntity<?> getRosterTemplateList(){
        var useCase = new GetRosterTemplateList();
        var rosterTemplateList = publishCollection(RosterTemplateModel.class, useCase);
        return ResponseEntity.ok().body(
            new Resource(RosterTemplateResponse.fromModels(rosterTemplateList))
        );
    }
}
