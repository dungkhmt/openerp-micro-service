package openerp.openerpresourceserver.entity.enumentity;

import java.util.Collections;
import java.util.Set;

public enum OrderItemStatus {
    PENDING,
    ASSIGNED,
    COLLECTED_COLLECTOR,
    COLLECTED_HUB,
    CONFIRMED_OUT,
    DELIVERING,
    DELIVERED,
    CONFIRMED_IN,
    ASSIGNED_SHIPPER,
    SHIPPED,
    COMPLETED,
    CANCELLED;
}
