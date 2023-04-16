package wms.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "product_unit")
public class ProductUnit extends BaseEntity {
    @Column(name = "code")
    private String code;
    @Column(name = "name")
    private String name;
}
