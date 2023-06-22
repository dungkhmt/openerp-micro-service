package openerp.containertransport.dto;

import jakarta.persistence.Column;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContainerModel {
    private long id;
    private String uid;
    private String containerCode;
    private String typeContainerCode;
    private Integer size;
    private Long facilityId;
    private String facilityName;
    private FacilityResponsiveDTO facilityResponsiveDTO;
    private String status;
    private Boolean isEmpty;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
