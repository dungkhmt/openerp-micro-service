package openerp.openerpresourceserver.labtimetabling.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import openerp.openerpresourceserver.labtimetabling.entity.Assign;

import java.util.UUID;

@ToString
@Data
@NoArgsConstructor
public class AssignResponse {
    private UUID id;
    private Long class_id;
    private Long room_id;
    private Long week;
    private Long day_of_week;
    private Long start_slot;
    private Long period;
    private Long duration;
    private String semester;

    private String room;
    private String lesson;

    public AssignResponse(Assign assign){
        this.id = assign.getId();
        this.class_id = assign.getClass_id();
        this.room_id = assign.getRoom_id();
        this.week = assign.getWeek();
        this.day_of_week = assign.getDay_of_week();
        this.start_slot = assign.getStart_slot();
        this.room = assign.getRoom().getName();
        this.lesson = assign.getLesson().getNote();
        this.duration = Long.valueOf(assign.getLesson().getPeriod());
        this.period = assign.getPeriod();
    }
}
