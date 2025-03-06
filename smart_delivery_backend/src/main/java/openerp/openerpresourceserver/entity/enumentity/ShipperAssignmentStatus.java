package openerp.openerpresourceserver.entity.enumentity;

public enum ShipperAssignmentStatus {
    ASSIGNED,              // Initial assignment to shipper
    PICKED_UP,             // Shipper has picked up the package from hub
    IN_TRANSIT,            // Package is in transit to recipient
    DELIVERED,             // Successfully delivered
    FAILED_ATTEMPT,        // Delivery attempt failed
    RETURNED_TO_HUB,       // Package returned to hub
    COMPLETED,             // Assignment is complete (delivery or return confirmed)
    CANCELED               // Assignment canceled
}