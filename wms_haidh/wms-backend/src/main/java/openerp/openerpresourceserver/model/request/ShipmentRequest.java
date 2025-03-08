package openerp.openerpresourceserver.model.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentRequest {
	private LocalDateTime expectedDeliveryStamp;
    private String createdBy;
}
