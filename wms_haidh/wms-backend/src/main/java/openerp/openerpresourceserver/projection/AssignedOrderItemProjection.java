package openerp.openerpresourceserver.projection;

public interface AssignedOrderItemProjection {
    String getWarehouseName();
    String getBayCode();
    String getLotId();
    Integer getQuantity();
    String getStatus();
}
