package openerp.openerpresourceserver.entity.projection;

public interface AssignedOrderItemProjection {
    String getWarehouseName();
    String getBayCode();
    String getLotId();
    Integer getQuantity();
    String getStatus();
}
