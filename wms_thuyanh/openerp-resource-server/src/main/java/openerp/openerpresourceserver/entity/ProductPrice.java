package openerp.openerpresourceserver.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "wms_product_price")
public class ProductPrice {
    @Id
    private UUID productPriceId;
    private UUID productId;
    private BigDecimal price;
    private Date startDate;
    private Date endDate;
    private String description;
}
