package wms.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "product_price")
public class ProductPrice {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_code")
    private Product productCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_type_code")
    private ContractType contractTypeCode;

    @Column(name = "price_before_vat")
    private Double priceBeforeVat;

    @Column(name = "vat")
    private Double vat;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "valid_from")
    private Instant validFrom;

    @Column(name = "valid_to")
    private Instant validTo;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

}