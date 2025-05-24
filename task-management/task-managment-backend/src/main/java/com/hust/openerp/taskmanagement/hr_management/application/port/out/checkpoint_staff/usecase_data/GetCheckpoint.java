package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_staff.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_staff.filter.ICheckpointStaffFilter;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
public class GetCheckpoint implements ICheckpointStaffFilter, UseCase {
    private String userId;
    private UUID periodId;

    @Override
    public List<String> getUserIds() {
        return null;
    }
}
