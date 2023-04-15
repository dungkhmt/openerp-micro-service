package wms.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class ProductImageId implements Serializable {
    private static final long serialVersionUID = 7537972093954257140L;
    @Column(name = "product_code", nullable = false, length = 20)
    private String productCode;

    @Column(name = "image_id", nullable = false)
    private Integer imageId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProductImageId entity = (ProductImageId) o;
        return Objects.equals(this.productCode, entity.productCode) &&
                Objects.equals(this.imageId, entity.imageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productCode, imageId);
    }

}