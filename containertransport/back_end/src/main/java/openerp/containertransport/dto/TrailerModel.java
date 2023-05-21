package openerp.containertransport.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrailerModel {
    private long id;
    private String trailerCode;
    private Integer facilityId;
    private String statusId;
    private Integer truckId;
    private long createdAt;
    private long updatedAt;
}
