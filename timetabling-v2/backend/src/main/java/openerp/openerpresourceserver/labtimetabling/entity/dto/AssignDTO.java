package openerp.openerpresourceserver.labtimetabling.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.labtimetabling.entity.Class;
import openerp.openerpresourceserver.labtimetabling.entity.Room;

import java.util.UUID;

@Data
@NoArgsConstructor
public class AssignDTO {
    private UUID id;
    private Long class_id;
    private Long room_id;
    private Long week;
    private Long day_of_week;
    private Long start_slot;
    private Long semester_id;
    private Long period;
    private Room room;
    private Class lesson;
}
