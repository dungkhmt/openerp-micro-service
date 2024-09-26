package wms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "scm_product")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity extends BaseEntity implements Serializable {
    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "unit_per_box")
    private Integer unitPerBox;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "unit_code", referencedColumnName = "code")
    @NotFound(action = NotFoundAction.IGNORE)
    private ProductUnit productUnit;

    @Column(name = "brand")
    private String brand;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_code", referencedColumnName = "code")
    @NotFound(action = NotFoundAction.IGNORE)
    private ProductCategory productCategory;

    @Column(name = "status")
    private String status;

    @Column(name = "mass_quantity")
    private int massQuantity;

    // TODO: Should define images field here
    @Column(name = "sku")
    private String sku;

    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY
//            cascade = CascadeType.ALL,
//            orphanRemoval = true
    )
//    @JsonManagedReference
    @JsonIgnore
    private List<ProductFacility> productFacilities;

    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY)
    // Add JsonIgnore: https://stackoverflow.com/questions/20813496/tomcat-exception-cannot-call-senderror-after-the-response-has-been-committed
    @JsonIgnore
    private Set<PurchaseOrderItem> purchaseOrderItems;

    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY)
    // Add JsonIgnore: https://stackoverflow.com/questions/20813496/tomcat-exception-cannot-call-senderror-after-the-response-has-been-committed
    @JsonIgnore
    private Set<InventoryItem> inventoryItems;

    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY)
    // Add JsonIgnore: https://stackoverflow.com/questions/20813496/tomcat-exception-cannot-call-senderror-after-the-response-has-been-committed
    @JsonIgnore
    private Set<ReceiptBillItem> receiptBillItems;

    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY)
    // Add JsonIgnore: https://stackoverflow.com/questions/20813496/tomcat-exception-cannot-call-senderror-after-the-response-has-been-committed
    @JsonIgnore
    private Set<SaleOrderItem> saleOrderItems;


    @OneToMany(mappedBy = "productEntity", fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    private List<ProductPrice> productPrices;

    @OneToMany(
            mappedBy = "productEntity",
            fetch = FetchType.LAZY
    )
    @JsonIgnore
    private List<ProductSalePrice> productSalePrices;
}
