package openerp.openerpresourceserver.projection;

import java.util.UUID;

public interface CustomerOrderProjection {
	UUID getCustomerAddressId();
    String getCustomerName();
    String getCustomerPhoneNumber();
    String getAddressName();
    Double getLongitude();
    Double getLatitude();
}
