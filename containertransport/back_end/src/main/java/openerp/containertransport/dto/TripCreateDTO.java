package openerp.containertransport.dto;

import lombok.*;

import java.math.BigDecimal;

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
