package wms.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Entity
@Table(name = "scm_product_facility")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductFacility extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_code", referencedColumnName = "code")
    @NotFound(action = NotFoundAction.IGNORE)
//    @JsonBackReference
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_code", referencedColumnName = "code")
    @NotFound(action = NotFoundAction.IGNORE)
//    @JsonBackReference
    private Facility facility;

    @Column(name = "inventory_qty")
    private int inventoryQty;

    @Column(name = "qty_threshold")
    private int qtyThreshold;
}
