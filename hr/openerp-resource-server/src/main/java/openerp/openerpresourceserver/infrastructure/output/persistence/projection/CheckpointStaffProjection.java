package openerp.openerpresourceserver.infrastructure.output.persistence.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface CheckpointStaffProjection {
    String getUserId();
    UUID getCheckpointPeriodId();
    String getCheckpointCode();
    BigDecimal getPoint();
    String getCheckedByUserId();
    BigDecimal getCoefficient();
    String getPeriodStatus();
    String getConfigureName();
    String getConfigureDescription();
    String getConfigureStatus();
}
