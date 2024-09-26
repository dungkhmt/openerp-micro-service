package wms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "scm_customer")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends BaseEntity implements Serializable {
    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_type_code", referencedColumnName = "code")
    @NotFound(action = NotFoundAction.IGNORE)
    private CustomerType customerType;

    @Column(name = "phone_number")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "province")
    private String province;

    @Column(name = "status")
    private String status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "facility_code", referencedColumnName = "code")
    @NotFound(action = NotFoundAction.IGNORE)
    private Facility facility;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", referencedColumnName = "user_login_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private UserRegister user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "contract_type_code", referencedColumnName = "code")
    @NotFound(action = NotFoundAction.IGNORE)
    private ContractType contractType;

    @OneToMany(mappedBy = "customer",fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<SaleOrder> saleOrders;
}
