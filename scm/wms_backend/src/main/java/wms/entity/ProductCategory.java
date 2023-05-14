package wms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "scm_product_category")
public class ProductCategory extends BaseEntity implements Serializable {
    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "productCategory",fetch = FetchType.LAZY)
    // Add JsonIgnore: https://stackoverflow.com/questions/20813496/tomcat-exception-cannot-call-senderror-after-the-response-has-been-committed
    @JsonIgnore
    private Set<ProductEntity> product;
}
