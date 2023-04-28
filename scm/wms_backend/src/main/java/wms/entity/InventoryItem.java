package wms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "scm_inventory_item")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItem extends BaseEntity implements Serializable {
    @Column(name = "code")
    private String code;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "facility_code", referencedColumnName = "code")
    @NotFound(action = NotFoundAction.IGNORE)
    private Facility facility;

    @Column(name = "lot_code")
    private String lotCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_code", referencedColumnName = "code")
    @NotFound(action = NotFoundAction.IGNORE)
    private ProductEntity product;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "receiving_date")
    private String receivingDate;

    @Column(name = "expire_date")
    private String expireDate;

    @OneToMany(mappedBy = "inventoryItem",fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ExportInventoryItem> exportInventoryItems = new HashSet<>();
}
