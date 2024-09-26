package openerp.containertransport.dto;

import jakarta.persistence.Column;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

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
    private String owner;
    private Boolean isEmpty;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private List<String> listContainerCode;
}
