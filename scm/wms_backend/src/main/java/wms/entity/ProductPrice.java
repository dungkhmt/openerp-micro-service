package wms.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "scm_product_price_sellin")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPrice extends BaseEntity implements Serializable {
    @Column(name = "started_date")
    private ZonedDateTime startedDate;
    @Column(name = "ended_date")
    private ZonedDateTime endedDate;
    @Column(name = "price_before_vat")
    private double priceBeforeVat;
    @Column(name = "vat")
    private double vat;
    @Column(name = "status")
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "product_code", referencedColumnName = "code")
    private ProductEntity productEntity;
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "contract_type_code", referencedColumnName = "code")
//    @NotFound(action = NotFoundAction.IGNORE)
//    private ContractType contractType;

}
