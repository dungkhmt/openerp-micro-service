package openerp.openerpresourceserver.labtimetabling.entity.autoscheduling;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.labtimetabling.entity.Class;
import openerp.openerpresourceserver.labtimetabling.entity.Room;
import openerp.openerpresourceserver.labtimetabling.entity.Semester_;

import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name = "timetable_lab_auto_scheduling_result")
@NoArgsConstructor
public class AutoSchedulingResult {
    @Id
    private UUID id;

    private Long class_id;
    private Long room_id;
    private Long week;
    private Long day_of_week;
    private Long start_slot;
    private Long period;

    private UUID submission_id;

    public AutoSchedulingResult(AutoSchedulingVar var){
        this.class_id = var.getClass_id();
        this.room_id = var.getRoom_id();
        this.week = var.getWeek();
        this.day_of_week = var.getPeriod()/2 + 2;
        this.period = var.getPeriod()%2 +1;
        this.start_slot = var.getLesson();
    }

    @Column(name = "created_time")
    private Date created_time;

//    @JsonIgnore
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "class_id", insertable = false, updatable = false)
//    private Class lesson;
//
//    @JsonIgnore
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "room_id", insertable = false, updatable = false)
//    private Room room;
}
