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
@Table(name = "wms_sale_order_item")
public class SaleOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID saleOrderItemId;

    private UUID orderId;

    private UUID productId;

    private int quantity;

    private double priceUnit; // in VND
    
    private double completed; // in percentage
    
    private LocalDateTime lastUpdated;
}

