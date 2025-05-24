package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_configure.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_configure.filter.ICheckpointConfigureFilter;
import com.hust.openerp.taskmanagement.hr_management.constant.CheckpointConfigureStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.IPageableRequest;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class GetAllCheckpointConfigure implements ICheckpointConfigureFilter, UseCase {
    private String name;
    private CheckpointConfigureStatus status;
    private IPageableRequest pageableRequest;

}
