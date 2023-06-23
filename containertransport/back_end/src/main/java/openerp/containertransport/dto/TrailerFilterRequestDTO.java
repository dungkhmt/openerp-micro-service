package openerp.containertransport.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrailerFilterRequestDTO {
    private String facilityId;
    private String status;
    private Integer truckId;
    private Integer page;
    private Integer pageSize;
}
