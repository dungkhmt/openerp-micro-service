package openerp.containertransport.dto;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContainerModel {
    private long id;
    private String containerCode;
    private Integer size;
    private long facilityId;
    private String facilityName;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
