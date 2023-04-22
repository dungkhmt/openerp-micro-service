package wms.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "inventory_item")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItem extends BaseEntity{
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
    private ZonedDateTime receivingDate;
}
