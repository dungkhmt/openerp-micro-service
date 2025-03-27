package openerp.openerpresourceserver.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PickupOrdersRequest {
    private List<UUID> orderItemIds;
    private UUID tripId;

    // Getters and setters
    public List<UUID> getOrderItemIds() {
        return orderItemIds;
    }

    public void setOrderIds(List<UUID> orderIds) {
        this.orderItemIds = orderIds;
    }

    public UUID getTripId() {
        return tripId;
    }

    public void setTripId(UUID tripId) {
        this.tripId = tripId;
    }
}
