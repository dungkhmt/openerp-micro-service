package wms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "scm_contract_type")
public class ContractType extends BaseEntity implements Serializable {
    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "channel_code", referencedColumnName = "code")
    @NotFound(action = NotFoundAction.IGNORE)
    private DistributingChannel channel;

    @OneToMany(mappedBy = "contractType",fetch = FetchType.LAZY)
    // Add JsonIgnore: https://stackoverflow.com/questions/20813496/tomcat-exception-cannot-call-senderror-after-the-response-has-been-committed
    @JsonIgnore
    private Set<Customer> customers;

//    @OneToMany(
//            mappedBy = "contractType",
//            fetch = FetchType.LAZY
//    )
//    @JsonIgnore
//    private List<ProductPrice> productPrices;

    @OneToMany(
            mappedBy = "contractType",
            fetch = FetchType.LAZY
    )
    @JsonIgnore
    private List<ProductSalePrice> productSalePrices;

}
