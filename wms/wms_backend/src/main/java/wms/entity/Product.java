package wms.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "product")
public class Product {
    @Id
    @Column(name = "code", nullable = false, length = 20)
    private String id;

    @Column(name = "id", nullable = false)
    private Integer id1;

    @Column(name = "name", length = 500)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_code")
    private ProductUnit unitCode;

    @Column(name = "unit_per_box")
    private Integer unitPerBox;

    @Column(name = "brand", length = 200)
    private String brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_code")
    private ProductCategory categoryCode;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "mass_type", length = 20)
    private String massType;

    @Column(name = "sku", length = 500)
    private String sku;

}