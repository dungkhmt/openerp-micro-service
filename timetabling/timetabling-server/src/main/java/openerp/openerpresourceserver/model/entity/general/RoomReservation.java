package openerp.openerpresourceserver.model.entity.general;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoomReservation {
    @JsonProperty("start_time")
    private int startTime;
    @JsonProperty("end_time")
    private int endTime;

    private int weekday;
    private String room;


    public RoomReservation(int startTime, int endTime, int weekday, String room) {
        this.endTime = endTime;
        this.startTime = startTime;
        this.weekday = weekday;
        this.room = room;
    }
    
    @Override
    public String toString() {
        return startTime + "-" + endTime + "/" + "D" + weekday+ "/" + room;
    }


}
