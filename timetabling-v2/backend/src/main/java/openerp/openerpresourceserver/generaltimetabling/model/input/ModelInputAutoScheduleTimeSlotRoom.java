package openerp.openerpresourceserver.generaltimetabling.model.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelInputAutoScheduleTimeSlotRoom {
    List<Long> classIds;
    private String semester;
    private int timeLimit;
}

