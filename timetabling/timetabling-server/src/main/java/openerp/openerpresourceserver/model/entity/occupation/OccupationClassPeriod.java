package openerp.openerpresourceserver.model.entity.occupation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OccupationClassPeriod {
    private long startPeriodIndex;
    private long endPeriodIndex;
    private String classCode;

    @Override
    public String toString() {
        return classCode + ":" + startPeriodIndex + "->" + endPeriodIndex;
    }
}