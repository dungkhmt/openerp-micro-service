package openerp.openerpresourceserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.sound.midi.Receiver;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "smartdelivery_order")
public class Order {
    @Id
    @GenericGenerator(name = "uuid1", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue
    private UUID id;


    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinColumn(name = "sender_id")
    @ToString.Exclude
    private Sender sender;


    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinColumn(name = "recipient_id")
    @ToString.Exclude
    private Recipient recipient;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "collector_id")
    @ToString.Exclude
    private Collector collector;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "shipper_id")
    @ToString.Exclude
    private Shipper shipper;

    private String orderType;

    private OrderStatus status;

    private BigDecimal totalPrice;

    private BigDecimal shipPrice;

    private BigDecimal finalPrice;

//    private String originAddress;
//
//    private String destinationAddress;


    private Date expectedDeliveryDate;

    @ManyToOne
    @JoinColumn(name = "origin_hub")
    @ToString.Exclude
    private Hub originHub;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    List<OrderItem> items;

    @ManyToOne
    @JsonIgnore
    private Hub finalHub;

    @CreatedBy
    private String createdBy;
    private String approvedBy;
    private String cancelledBy;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

}
