package openerp.containertransport.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacilityFilterRequestDTO {
    private String facilityName;
    private String facilityCode;
    private String status;
    private String type;
    private String typeOwner;
    private Integer page;
    private Integer pageSize;
    private String owner;
}
