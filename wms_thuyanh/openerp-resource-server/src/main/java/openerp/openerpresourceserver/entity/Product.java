package openerp.openerpresourceserver.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "wms_product")
public class Product {
    @Id
    private UUID productId;
    private String code;
    private String name;
    private String description;

    private BigDecimal height;
    private BigDecimal weight;
    private BigDecimal area;

    private String uom;
    private UUID categoryId;

    private String imageContentType;
    private Long imageSize;
    @Lob
    private byte[] imageData;
}
