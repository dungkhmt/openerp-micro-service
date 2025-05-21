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
@Table(name = "wms_receipt_item_request")
public class ReceiptItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID receiptItemRequestId;

    private UUID receiptId;        
    private UUID productId;        
    private int quantity;         
    private UUID warehouseId;   
    private double completed; // in percentage
    private LocalDateTime lastUpdated;
}

