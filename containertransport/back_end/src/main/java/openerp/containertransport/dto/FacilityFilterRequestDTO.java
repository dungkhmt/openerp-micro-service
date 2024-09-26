package openerp.containertransport.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacilityFilterRequestDTO {
    private String facilityName;
    private String facilityCode;
    private String status;
    private String type;
    private List<String> typeOwner;
    private Integer page;
    private Integer pageSize;
    private String owner;
}
