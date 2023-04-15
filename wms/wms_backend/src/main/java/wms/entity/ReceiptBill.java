package wms.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "receipt_bill")
public class ReceiptBill {
    @Id
    @Column(name = "code", nullable = false, length = 20)
    private String id;

    @Column(name = "id", nullable = false)
    private Integer id1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_code")
    private PurchaseOrder orderCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_code")
    private Facility facilityCode;

    @Column(name = "receiving_date")
    private Instant receivingDate;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

}