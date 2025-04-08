package openerp.openerpresourceserver.projection;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ProductGeneralProjection {
	UUID getId();
    String getCode();
    String getName();
    LocalDateTime getDateUpdated();
}

