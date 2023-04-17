package openerp.containertransport.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TruckModel {
    private long id;
    private String truckCode;
    private Integer facilityId;
    private Integer driverId;
    private String licensePlates;
    private String brandTruck;
    private long createdAt;
    private long updatedAt;
}
