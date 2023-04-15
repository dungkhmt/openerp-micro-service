package wms.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "product_facility")
public class ProductFacility {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_code")
    private Facility facilityCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_code")
    private Product productCode;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

}