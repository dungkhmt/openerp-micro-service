package openerp.openerpresourceserver.labtimetabling.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.labtimetabling.entity.ScheduleConflict;
import openerp.openerpresourceserver.labtimetabling.entity.constant.ConflictType;

@Data
@NoArgsConstructor
public class ScheduleConflictResponse {
    private AssignResponse assignResponse;
    private ConflictType conflictType;

    public ScheduleConflictResponse(ScheduleConflict scheduleConflict){
        this.assignResponse = new AssignResponse(scheduleConflict.getAssign());
        this.conflictType = scheduleConflict.getConflictType();
    }
}
