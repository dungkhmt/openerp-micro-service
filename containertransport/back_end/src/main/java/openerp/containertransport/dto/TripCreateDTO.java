package openerp.containertransport.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripCreateDTO {
    private String shipmentId;
    private String createBy;
    private TripModel tripContents;
    private String type;
}
