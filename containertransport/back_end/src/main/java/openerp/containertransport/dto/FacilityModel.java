package openerp.containertransport.dto;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacilityModel {
    private long id;
    private String facilityCode;
    private String facilityName;
    private String facilityType;
    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Long processingTime;
    private Integer maxNumberTruck;
    private Integer maxNumberTrailer;
    private Integer maxNumberContainer;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
