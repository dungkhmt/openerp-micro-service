package openerp.openerpresourceserver.dto.request;

import java.util.UUID;

import lombok.Data;

@Data
public class AddressDistanceDTO {
    private UUID addressDistanceId;
    private CoordinateDTO from;
    private CoordinateDTO to;
    
    public AddressDistanceDTO(UUID addressDistanceId, CoordinateDTO from, CoordinateDTO to) {
        this.addressDistanceId = addressDistanceId;
        this.from = from;
        this.to = to;
    }

}
