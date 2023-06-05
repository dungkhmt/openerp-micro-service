package openerp.containertransport.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacilityFilterRequestDTO {
    private String facilityName;
    private String type;
    private Integer page;
    private Integer pageSize;
}
