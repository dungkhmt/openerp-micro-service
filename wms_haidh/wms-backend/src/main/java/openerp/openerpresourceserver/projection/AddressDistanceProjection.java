package openerp.openerpresourceserver.projection;

import java.util.UUID;

public interface AddressDistanceProjection {
    UUID getAddressDistanceId();
    String getFromLocationName();
    String getToLocationName();
    Double getDistance();
}

