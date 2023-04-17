package openerp.containertransport.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class ContainerModel {
    private long id;
    private String containerCode;
    private Integer facilityId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
