package wms.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "shipment")
public class Shipment {
    @Id
    @Column(name = "code", nullable = false, length = 20)
    private String id;

    @Column(name = "id", nullable = false)
    private Integer id1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private UserLogin createdBy;

    @Column(name = "created_date")
    private Instant createdDate;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

}