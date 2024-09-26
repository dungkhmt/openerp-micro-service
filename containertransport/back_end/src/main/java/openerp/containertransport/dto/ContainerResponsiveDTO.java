package openerp.containertransport.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContainerResponsiveDTO {
    private long containerId;
    private String containerCode;
    private boolean isEmpty;
    private Integer containerSize;
    private Long facilityId;
}
