package openerp.openerpresourceserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.enumentity.OrderItemStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;

import java.sql.Timestamp;
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
    private UUID orderId;
    @Column(nullable = false)
    private String name;
    private Integer quantity;
    @Column(nullable = false)
    private Double weight;
    private Double price;
    private Double length;
    private Double width;
    private Double height;
    @Enumerated(EnumType.STRING)
    private OrderItemStatus status;

    public OrderItem(String name, Integer quantity, Double weight, Double price, Double length, Double width, Double height) {
        this.name = name;
        this.quantity = quantity;
        this.weight = weight;
        this.price = price;
        this.length = length;
        this.width = width;
        this.height = height;
    }
    @CreatedBy
    private String createdBy;
    private String approvedBy;
    private String cancelledBy;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

}
