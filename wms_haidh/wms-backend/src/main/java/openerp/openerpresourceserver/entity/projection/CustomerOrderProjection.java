package openerp.openerpresourceserver.entity.projection;

public interface CustomerOrderProjection {
    String getCustomerName();
    String getAddressName();
    Double getLongitude();
    Double getLatitude();
    Double getTotalOrderCost();
    String getPaymentType();
}
