package openerp.openerpresourceserver.projection;

import java.time.LocalDateTime;

public interface DeliveryTripGeneralProjection {
    
    Double getDistance();
    
    Double getTotalWeight();
    
    Integer getTotalLocations();
    
    String getStatus();
    
    String getDescription();

    LocalDateTime getExpectedDeliveryStamp(); 

    String getWarehouseName();

    String getDeliveryPersonName(); 
}

