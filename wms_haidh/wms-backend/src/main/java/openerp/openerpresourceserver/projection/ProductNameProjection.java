package openerp.openerpresourceserver.projection;

import java.util.UUID;

public interface ProductNameProjection {
    UUID getProductId();
    String getName();
    String getUom();
}
