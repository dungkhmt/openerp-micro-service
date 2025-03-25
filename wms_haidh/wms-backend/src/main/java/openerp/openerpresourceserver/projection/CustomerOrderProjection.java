package openerp.openerpresourceserver.projection;

public interface CustomerOrderProjection {
    String getCustomerName();
    String getCustomerPhoneNumber();
    String getAddressName();
    Double getLongitude();
    Double getLatitude();
}
