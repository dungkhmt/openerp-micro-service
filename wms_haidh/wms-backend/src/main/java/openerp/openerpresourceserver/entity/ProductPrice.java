package openerp.openerpresourceserver.entity;

import java.time.LocalDate;
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

@Entity
@Data
@Builder
@Table(name = "wms_product_price")
@NoArgsConstructor
@AllArgsConstructor
public class ProductPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID productPriceId;
    private UUID productId;
    private double price; // in VND
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
}
