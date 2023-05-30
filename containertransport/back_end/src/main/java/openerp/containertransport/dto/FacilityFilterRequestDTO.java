package openerp.containertransport.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacilityFilterRequestDTO {
    private String facilityName;
    private Integer page;
    private Integer pageSize;
}
