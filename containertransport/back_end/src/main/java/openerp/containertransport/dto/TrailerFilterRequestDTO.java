package openerp.containertransport.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrailerFilterRequestDTO {
    private Integer facilityId;
    private String statusId;
    private Integer truckId;
}
