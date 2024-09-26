package openerp.containertransport.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FacilityResponsiveDTO {
    private long facilityId;
    private String facilityUid;
    private String facilityCode;
    private String facilityName;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Long processingTimePickUp;
    private Long processingTimeDrop;
    private String address;
}
