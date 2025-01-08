package openerp.openerpresourceserver.labtimetabling.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.labtimetabling.entity.dto.AssignDTO;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "timetable_lab_assign")
@Data
@NoArgsConstructor
public class Assign implements Serializable {
    @Id
    private UUID id;
    private Long class_id;
    private Long room_id;
    private Long week;
    private Long day_of_week;
    private Long start_slot;
    private Long semester_id;
    private Long period;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("classId")
    @JoinColumn(name = "class_id", insertable = false, updatable = false)
    private Class lesson;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("roomId")
    @JoinColumn(name = "room_id", insertable = false, updatable = false)
    private Room room;

    @ManyToOne
    @JoinColumn(name = "semester_id", insertable=false, updatable=false)
    private Semester_ semester;

    public Assign(AssignDTO dto){
        this.setId(dto.getId());

        this.setWeek(dto.getWeek());
        this.setDay_of_week(dto.getDay_of_week());
        this.setStart_slot(dto.getStart_slot());
        this.setPeriod(dto.getPeriod());

        this.setRoom_id(dto.getRoom_id());
        this.setSemester_id(dto.getSemester_id());
        this.setClass_id(dto.getClass_id());
        this.setRoom(new Room(dto.getRoom().getId()));
        this.setLesson(new Class(dto.getLesson().getId()));
    }
}
