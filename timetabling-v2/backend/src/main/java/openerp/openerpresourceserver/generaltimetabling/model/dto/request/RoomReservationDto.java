package openerp.openerpresourceserver.generaltimetabling.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomReservationDto {
    private Long parentId;
    private Integer duration;
    // Getter và Setter
    //public Integer getDuration() {
    //    return duration;
    //}

    //public void setDuration(Integer periods) {
    //    this.duration = periods;
    //}
}