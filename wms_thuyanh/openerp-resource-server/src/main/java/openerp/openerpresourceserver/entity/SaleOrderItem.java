package openerp.openerpresourceserver.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "wms_sale_order_item")
@EntityListeners(AuditingEntityListener.class)
public class SaleOrderItem {
    @Id
    private UUID saleOrderItemId;
    private UUID orderId;
    private UUID productId;
    private long quantity;
    private BigDecimal priceUnit;
}
