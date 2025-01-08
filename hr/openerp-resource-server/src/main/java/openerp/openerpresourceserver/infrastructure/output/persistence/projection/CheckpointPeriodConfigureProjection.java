package openerp.openerpresourceserver.infrastructure.output.persistence.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CheckpointPeriodConfigureProjection {
    String getCheckpointCode();
    String getName();
    String getDescription();
    String getStatus();
    BigDecimal getCoefficient();
}
