package openerp.openerpresourceserver.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSuggestionDto {

    // Basic order information
    private UUID orderId;
    private String orderCode;

    // Package details
    private Double weight;
    private Double volume;

    // Sender/Recipient info
    private String senderName;
    private String senderAddress;
    private String recipientName;
    private String recipientAddress;

    // Route information
    private UUID hubId;
    private String hubName;
    private Integer stopSequence;

    // Suggestion scoring
    private Integer priority;           // 0-100, higher is better
    private Integer fitScore;          // 0-100, how well it fits in vehicle
    private String recommendationReason;

    // Special requirements
    private Boolean isFragile;
    private Boolean isColdChain;
    private Boolean isUrgent;
    private Integer urgencyLevel;

    // Status information
    private String currentStatus;
    private String expectedDeliveryTime;

    // Capacity impact
    private Double remainingWeightAfter;
    private Double remainingVolumeAfter;
    private Double capacityUtilization;

    // Constructor for basic info
    public OrderSuggestionDto( UUID orderId, Double weight, Double volume) {
        this.orderId = orderId;
        this.weight = weight;
        this.volume = volume;
    }

    // Helper methods
    public boolean canFitInVehicle() {
        return fitScore > 0;
    }

    public boolean isHighPriority() {
        return priority >= 80;
    }

    public boolean isMediumPriority() {
        return priority >= 60 && priority < 80;
    }

    public String getPriorityLevel() {
        if (isHighPriority()) return "HIGH";
        if (isMediumPriority()) return "MEDIUM";
        return "STANDARD";
    }

    public String getFitLevel() {
        if (fitScore >= 80) return "EXCELLENT";
        if (fitScore >= 60) return "GOOD";
        if (fitScore > 0) return "ACCEPTABLE";
        return "CANNOT_FIT";
    }
}