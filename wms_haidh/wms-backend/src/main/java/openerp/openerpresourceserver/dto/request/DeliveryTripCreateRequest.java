package openerp.openerpresourceserver.dto.request;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.DeliveryTripItem;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryTripCreateRequest {
    private UUID warehouseId;
    private UUID vehicleId;
    private String deliveryPersonId;
    private String description;
    private String shipmentId;
    private Double totalWeight;
    private Integer totalLocations;
    private Double distance;
    private List<DeliveryTripItem> items;
    private List<CoordinateDTO> coordinates;
}

