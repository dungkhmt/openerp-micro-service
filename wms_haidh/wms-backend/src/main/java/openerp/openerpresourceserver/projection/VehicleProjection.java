package openerp.openerpresourceserver.projection;

import java.util.UUID;

public interface VehicleProjection {
    UUID getVehicleId();
    String getName();
    Double getMaxWeight();
}

