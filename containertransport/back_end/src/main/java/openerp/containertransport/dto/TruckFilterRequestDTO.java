package openerp.containertransport.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TruckFilterRequestDTO {
    private String truckCode;
    private String status;
    private Integer page;
    private Integer pageSize;
    private Long facilityId;
}
