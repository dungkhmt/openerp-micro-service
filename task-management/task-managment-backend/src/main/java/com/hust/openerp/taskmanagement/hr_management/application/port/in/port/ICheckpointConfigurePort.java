package com.hust.openerp.taskmanagement.hr_management.application.port.in.port;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_configure.filter.ICheckpointConfigureFilter;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointConfigureModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.IPageableRequest;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PageWrapper;

import java.util.List;

public interface ICheckpointConfigurePort extends ICodeGeneratorPort{
    CheckpointConfigureModel findByCode(String code);
    List<CheckpointConfigureModel> findByCodeIn(List<String> codes);
    CheckpointConfigureModel createCheckpointConfigure(CheckpointConfigureModel configure);
    void updateCheckpointConfigure(CheckpointConfigureModel configure);
    PageWrapper<CheckpointConfigureModel> getCheckpointConfigure
            (ICheckpointConfigureFilter filter, IPageableRequest request);
}
