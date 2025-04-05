package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "wms_product_price")
@NoArgsConstructor
@AllArgsConstructor
public class ProductPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID productPriceId;
    private UUID productId;
    private BigDecimal price;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
