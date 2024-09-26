package openerp.openerpresourceserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "smartdelivery_order_item")
public class OrderItem {
    @Id
    @GenericGenerator(name = "uuid1", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue
    private UUID orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private String name;

    private Long quantity;

    @Column(nullable = false)
    private BigDecimal weight;

    private BigDecimal price;

    private BigDecimal length;

    private BigDecimal width;

    private BigDecimal height;

    public OrderItem(String name, Long quantity, BigDecimal weight, BigDecimal price, BigDecimal length, BigDecimal width, BigDecimal height) {
        this.name = name;
        this.quantity = quantity;
        this.weight = weight;
        this.price = price;
        this.length = length;
        this.width = width;
        this.height = height;
    }
}
