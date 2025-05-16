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
@Table(name = "wms_receipt")
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID receiptId; 

    private String receiptName; 

    private String description; 

    private UUID warehouseId; 
    
    private UUID supplierId; 

    private LocalDateTime expectedReceiptDate; 

    private String status; 

    private String createdBy; 

    private String approvedBy;

    private String cancelledBy; 

    private LocalDateTime lastUpdatedStamp; 

    private LocalDateTime createdStamp; 
}


