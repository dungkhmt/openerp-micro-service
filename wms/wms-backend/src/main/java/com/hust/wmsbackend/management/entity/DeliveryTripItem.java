package com.hust.wmsbackend.management.entity;

import com.hust.wmsbackend.management.entity.enumentity.DeliveryTripItemStatus;
import com.hust.wmsbackend.management.entity.generator.StringPrefixedSequenceCodeGenerator;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "delivery_trip_item")
@EntityListeners(AuditingEntityListener.class)
public class DeliveryTripItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "delivery_trip_item_seq")
    @GenericGenerator(name = "delivery_trip_item_seq",
        strategy = "com.hust.wmsbackend.management.entity.generator.StringPrefixedSequenceCodeGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = StringPrefixedSequenceCodeGenerator.INCREMENT_PARAM, value = "1"),
            @org.hibernate.annotations.Parameter(name = StringPrefixedSequenceCodeGenerator.VALUE_PREFIX_PARAMETER, value = "TRP_ITEM_"),
            @org.hibernate.annotations.Parameter(name = StringPrefixedSequenceCodeGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d")
        })
    private String deliveryTripItemId;
    private String deliveryTripId;
    private int sequence;
    private UUID orderId;
    // từ sequence và orderId -> lấy được vị trí cần giao hàng tại vị trí [sequence] của shipper thực hiện delivery_trip này
    private UUID assignedOrderItemId;
    private BigDecimal quantity;
    private boolean isDeleted = false;
    @Enumerated(EnumType.STRING)
    private DeliveryTripItemStatus status = DeliveryTripItemStatus.CREATED;
}
