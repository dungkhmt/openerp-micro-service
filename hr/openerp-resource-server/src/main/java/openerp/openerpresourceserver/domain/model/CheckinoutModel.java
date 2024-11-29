package openerp.openerpresourceserver.domain.model;

import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.CheckinoutType;

import java.time.LocalDateTime;

@Getter
@Setter
public class CheckinoutModel {
    private String userId;
    private LocalDateTime pointTime;
    private CheckinoutType type;
}
