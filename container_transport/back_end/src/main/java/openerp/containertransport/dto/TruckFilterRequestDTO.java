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
}
