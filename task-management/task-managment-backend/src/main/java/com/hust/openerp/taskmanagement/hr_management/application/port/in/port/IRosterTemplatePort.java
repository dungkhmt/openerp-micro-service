package com.hust.openerp.taskmanagement.hr_management.application.port.in.port;

import com.hust.openerp.taskmanagement.hr_management.domain.model.RosterTemplateModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.RosterTemplateEntity;

import java.util.List;
import java.util.UUID;

public interface IRosterTemplatePort {
    RosterTemplateModel createRosterTemplate(RosterTemplateModel model);

    void cancelRosterTemplate(UUID id);

    RosterTemplateModel getRosterTemplate(UUID id);

    List<RosterTemplateModel> getRosterTemplates();

    RosterTemplateModel updateRosterTemplate(RosterTemplateModel model);

    RosterTemplateEntity getRosterTemplateEntity(UUID id);
}
