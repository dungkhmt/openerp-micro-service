package wms.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "product_image")
public class ProductImage {
    @EmbeddedId
    private ProductImageId id;

    @MapsId("productCode")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_code", nullable = false)
    private Product productCode;

    @MapsId("imageId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "image_id", nullable = false)
    private ImageUpload image;

}