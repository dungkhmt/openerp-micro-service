package openerp.openerpresourceserver.entity;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProductProjection {
    UUID getProductId();
    String getCode();
    String getName();
    String getDescription();
    BigDecimal getHeight();
    BigDecimal getWeight();
    BigDecimal getArea();
    String getUom();
    UUID getCategoryId();
    String getImageContentType();
    Long getImageSize();
}

