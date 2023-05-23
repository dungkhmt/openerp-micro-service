package openerp.containertransport.dto;

import lombok.*;
import openerp.containertransport.entity.Facility;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrailerModel {
    private long id;
    private String trailerCode;
    private Integer facilityId;
    private FacilityResponsiveDTO facilityResponsiveDTO;
    private String status;
    private Integer truckId;
    private long createdAt;
    private long updatedAt;
}
