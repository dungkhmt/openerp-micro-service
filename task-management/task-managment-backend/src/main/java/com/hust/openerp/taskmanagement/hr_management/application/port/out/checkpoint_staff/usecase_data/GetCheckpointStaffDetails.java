package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_staff.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_staff.filter.ICheckpointStaffFilter;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
public class GetCheckpointStaffDetails implements UseCase {
    private String userId;
    private UUID periodId;
}
