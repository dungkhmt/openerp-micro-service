package openerp.containertransport.dto;

import lombok.*;
import openerp.containertransport.entity.Facility;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TruckModel {
    private long id;
    private String truckCode;
    private Long facilityId;
    private String facilityName;
    private Integer driverId;
    private String driverName;
    private String licensePlates;
    private String brandTruck;
    private long createdAt;
    private long updatedAt;
}
