package openerp.openerpresourceserver.dto.response;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TruckDTO {
    private UUID vehicleId;
    private UUID warehouse;
    private double totalWeight;
    private double maxWeight;
    private double routeDistance;
    private List<UUID> route;
    private List<UUID> loadedItems;
    private List<Integer> itemSequences;

}

