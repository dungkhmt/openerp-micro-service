package openerp.openerpresourceserver.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "wms_assigned_order_item")
public class AssignedOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID assignedOrderItemId;

    private UUID orderId;

    private UUID productId;

    private int quantity;

    private UUID bayId;

    private UUID warehouseId;

    private String assignedBy;

    private LocalDateTime lastUpdatedStamp;

    private LocalDateTime createdStamp;

    private String lotId;

    private String status;

    private UUID inventoryItemId;

}

