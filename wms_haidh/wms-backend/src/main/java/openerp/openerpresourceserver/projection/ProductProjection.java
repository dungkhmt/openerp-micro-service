package openerp.openerpresourceserver.projection;

import java.util.UUID;

public interface ProductProjection {
    UUID getProductId();
    String getName();
    String getImageUrl();
    double getPrice();
    double getQuantity();
    String getUom();
    double getWeight();
}
