package wms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "scm_product_unit")
public class ProductUnit extends BaseEntity implements Serializable {
    @Column(name = "code")
    private String code;
    @Column(name = "name")
    private String name;
    @OneToMany(mappedBy = "productUnit",fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ProductEntity> product;
}
