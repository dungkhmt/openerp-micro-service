package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.adapter;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IRosterTemplatePort;
import com.hust.openerp.taskmanagement.hr_management.domain.exception.ApplicationException;
import com.hust.openerp.taskmanagement.hr_management.domain.model.RosterTemplateModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.RosterTemplateEntity;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository.RosterTemplateRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class RosterTemplateAdapter implements IRosterTemplatePort {
    private final RosterTemplateRepo rosterTemplateRepo;

    @Override
    public RosterTemplateModel createRosterTemplate(RosterTemplateModel model) {
        var rosterTemplateEntity = new RosterTemplateEntity();
        rosterTemplateEntity.setTemplateName(model.getTemplateName());
        rosterTemplateEntity.setDefinedShifts(model.getDefinedShifts());
        rosterTemplateEntity.setActiveHardConstraints(model.getActiveHardConstraints());
        return toModel(rosterTemplateRepo.save(rosterTemplateEntity));
    }

    @Override
    public void cancelRosterTemplate(UUID id) {
        rosterTemplateRepo.deleteById(id);
    }

    @Override
    public RosterTemplateModel getRosterTemplate(UUID id) {
        return toModel(getRosterTemplateEntity(id));
    }

    @Override
    public List<RosterTemplateModel> getRosterTemplates() {
        return toModels(rosterTemplateRepo.findAll());
    }

    @Override
    public RosterTemplateModel updateRosterTemplate(RosterTemplateModel model) {
        var rosterTemplateEntity = getRosterTemplateEntity(model.getId());
        if(model.getTemplateName() != null) {
            rosterTemplateEntity.setTemplateName(model.getTemplateName());
        }
        if(model.getDefinedShifts() != null) {
            rosterTemplateEntity.setDefinedShifts(model.getDefinedShifts());
        }
        if(model.getActiveHardConstraints() != null) {
            rosterTemplateEntity.setActiveHardConstraints(model.getActiveHardConstraints());
        }
        if(model.getDepartmentFilter() != null){
            //rosterTemplateEntity.setDepartmentFilter(model.getDepartmentFilter());
        }
        if(model.getJobPositionFilter() != null){
            //rosterTemplateEntity.setJobPositionFilter(model.getJobPositionFilter());
        }
        rosterTemplateEntity.setTemplateName(model.getTemplateName());
        rosterTemplateEntity.setDefinedShifts(model.getDefinedShifts());
        rosterTemplateEntity.setActiveHardConstraints(model.getActiveHardConstraints());
        return toModel(rosterTemplateRepo.save(rosterTemplateEntity));
    }

    @Override
    public RosterTemplateEntity getRosterTemplateEntity(UUID id) {
        return rosterTemplateRepo.findById(id).orElseThrow(
            () -> new ApplicationException("RosterTemplate with id " + id + " not found")
        );
    }

    private RosterTemplateModel toModel(RosterTemplateEntity rosterTemplateEntity) {
        return RosterTemplateModel.builder()
            .id(rosterTemplateEntity.getId())
            .templateName(rosterTemplateEntity.getTemplateName())
            .definedShifts(rosterTemplateEntity.getDefinedShifts())
            .activeHardConstraints(rosterTemplateEntity.getActiveHardConstraints())
            .build();
    }

    private List<RosterTemplateModel> toModels(List<RosterTemplateEntity> entities) {
        return entities.stream()
            .map(this::toModel)
            .toList();
    }
}
