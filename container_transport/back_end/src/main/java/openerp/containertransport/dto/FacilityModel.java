package openerp.containertransport.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class FacilityModel {
    private Long id;
    private String facilityCode;
    private String facilityName;
    private String facilityType;
    private Integer maxNumberTruck;
    private Integer maxNumberTrailer;
    private Integer maxNumberContainer;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
