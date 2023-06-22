package openerp.containertransport.dto;

import jakarta.persistence.Column;
import lombok.*;
import openerp.containertransport.entity.Facility;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrailerModel {
    private long id;
    private String uid;
    private String trailerCode;
    private Integer facilityId;
    private FacilityResponsiveDTO facilityResponsiveDTO;
    private String status;
    private String truckUid;
    private long createdAt;
    private long updatedAt;
}
