package openerp.openerpresourceserver.firstyeartimetabling.entity.firstyear;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "first_year_timetabling_room_reservations")
@Entity
@Builder
public class FirstYearRoomReservation {
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "first_year_class_id")
    @JsonIgnore
    private FirstYearClass firstYearClass;
    @JsonProperty("crew")
    private String crew;

    @JsonProperty("startTime")
    private Integer startTime;
    @JsonProperty("endTime")
    private Integer endTime;

    private Integer weekday;
    private String room;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public FirstYearRoomReservation(String crew, int startTime, int endTime, int weekday, String room) {
        this.crew = crew;
        this.endTime = endTime;
        this.startTime = startTime;
        this.weekday = weekday;
        this.room = room;
    }

    public FirstYearRoomReservation(FirstYearClass firstYearClass, String crew, int startTime, int endTime, int weekday, String room) {
        this.firstYearClass = firstYearClass;
        this.crew = crew;
        this.startTime = startTime;
        this.endTime = endTime;
        this.weekday = weekday;
        this.room = room;
    }

    @Override
    public String toString() {
        return firstYearClass.getClassCode() + ":" + startTime + "-" + endTime + "/" + "D" + weekday+ "/" + room;
    }


    public boolean isScheduleNotNull() {
        return this.getStartTime() != null && this.getEndTime() != null && this.getWeekday() != null && this.getRoom()!= null && !this.getRoom().isEmpty();
    }

    public boolean isTimeSlotNotNull() {
        return this.getStartTime() != null && this.getEndTime() != null && this.getWeekday() != null;
    }
}
