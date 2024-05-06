package openerp.openerpresourceserver.generaltimetabling.model.entity.general;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "room_reservation")
@Entity
@Builder
public class RoomReservation {
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "general_class_opened_id")
    @JsonIgnore
    private GeneralClassOpened generalClassOpened;
    @JsonProperty("startTime")
    private Integer startTime;
    @JsonProperty("endTime")
    private Integer endTime;

    private Integer weekday;
    private String room;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public RoomReservation(int startTime, int endTime, int weekday, String room) {
        this.endTime = endTime;
        this.startTime = startTime;
        this.weekday = weekday;
        this.room = room;
    }

    public RoomReservation(GeneralClassOpened gClass, int startTime, int endTime, int weekday, String room) {
        this.generalClassOpened = gClass;
        this.startTime = startTime;
        this.endTime = endTime;
        this.weekday = weekday;
        this.room = room;
    }

    @Override
    public String toString() {
        return generalClassOpened.getClassCode() + ":" + startTime + "-" + endTime + "/" + "D" + weekday+ "/" + room;
    }


    public boolean isScheduleNotNull() {
        return this.getStartTime() != null && this.getEndTime() != null && this.getWeekday() != null && this.getRoom()!= null && !this.getRoom().isEmpty();
    }
}
