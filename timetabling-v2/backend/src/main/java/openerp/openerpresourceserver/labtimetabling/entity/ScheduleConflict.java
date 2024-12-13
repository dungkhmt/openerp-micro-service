package openerp.openerpresourceserver.labtimetabling.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import openerp.openerpresourceserver.labtimetabling.entity.constant.ConflictType;

@Data
@AllArgsConstructor
public class ScheduleConflict {
    private Assign assign;
    private ConflictType conflictType;
}
