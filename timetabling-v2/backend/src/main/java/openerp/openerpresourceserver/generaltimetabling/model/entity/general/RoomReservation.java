package openerp.openerpresourceserver.generaltimetabling.model.entity.general;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "timetabling_room_reservations")
@Entity
@Builder
public class RoomReservation {
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "general_class_id")
    @JsonIgnore
    private GeneralClass generalClass;
    private String crew;
    private Integer startTime;
    private Integer endTime;
    private Integer weekday;
    private String room;
    private Integer duration;
    private Long parentId;
    //public String toString(){
    //    return "crew[" + crew + "],start[" + startTime + "],end[" + endTime + "],weekday[" + weekday + "],room[" + room + "],duration[" + duration + "]";
    //}
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public RoomReservation(String crew, int startTime, int endTime, int weekday, String room) {
        this.crew = crew;
        this.endTime = endTime;
        this.startTime = startTime;
        this.weekday = weekday;
        this.room = room;
    }

    public RoomReservation(GeneralClass gClass,String crew,  int startTime, int endTime, int weekday, String room) {
        this.generalClass = gClass;
        this.crew = crew;
        this.startTime = startTime;
        this.endTime = endTime;
        this.weekday = weekday;
        this.room = room;
    }

    @Override
    public String toString() {
        return generalClass.getClassCode() + ":" + startTime + "-" + endTime + "/" + "D" + weekday+ "/" + room;
    }

    @Override
    public boolean equals(Object obj) {
        RoomReservation that = (RoomReservation) obj;
        return this.id.equals(that.id);
    }

    public boolean isScheduleNotNull() {
        return this.getStartTime() != null && this.getEndTime() != null && this.getWeekday() != null && this.getRoom()!= null && !this.getRoom().isEmpty();
    }

    public boolean isTimeSlotNotNull() {
        return this.getStartTime() != null && this.getEndTime() != null && this.getWeekday() != null;
    }
}
