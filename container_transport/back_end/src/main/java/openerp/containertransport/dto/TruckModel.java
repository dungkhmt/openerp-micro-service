package openerp.containertransport.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import openerp.containertransport.entity.Facility;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TruckModel implements Serializable {
    private long id;
    private String truckCode;
    private Long facilityId;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private FacilityModel facility;
    private String facilityName;
    private Integer driverId;
    private String driverName;
    private String licensePlates;
    private String brandTruck;
    private long createdAt;
    private long updatedAt;
}
