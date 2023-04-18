package openerp.containertransport.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
