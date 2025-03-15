package com.hust.openerp.taskmanagement.hr_management.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CheckpointModel {
    private UUID periodId;
    private String userId;
    List<CheckpointStaffModel> checkpointStaffs;
    private BigDecimal totalPoint;

    public String getCheckedByUserId(){
        if(checkpointStaffs == null || checkpointStaffs.isEmpty()){
            return null;
        }
        return checkpointStaffs.get(0).getCheckedByUserId();
    }
}
