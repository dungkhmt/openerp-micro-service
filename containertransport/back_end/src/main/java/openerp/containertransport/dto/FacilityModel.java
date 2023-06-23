package openerp.containertransport.dto;

import jakarta.persistence.Column;
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
    private String uid;
    private String facilityCode;
    private String facilityName;
    private String facilityType;
    private String address;
    private String owner;
    private BigDecimal acreage;
    private Integer numberTruck;
    private Integer numberTrailer;
    private Integer numberContainer;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Long processingTimePickUp;
    private Long processingTimeDrop;
    private Integer maxNumberTruck;
    private Integer maxNumberTrailer;
    private Integer maxNumberContainer;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
