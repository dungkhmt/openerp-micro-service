package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.EmployeeDTO;
import openerp.openerpresourceserver.dto.OrderRequestDto;
import openerp.openerpresourceserver.dto.OrderResponseCollectorShipperDto;
import openerp.openerpresourceserver.dto.OrderSummaryDTO;
import openerp.openerpresourceserver.entity.enumentity.ShipperAssignmentStatus;

import java.util.List;
import java.util.UUID;

public interface ShipperAssignmentService {

    /**
     * Get orders that are ready for delivery at a specific hub
     */
    List<OrderSummaryDTO> getPendingDeliveryOrders(UUID hubId);
    /**
     * Auto-assign orders to available shippers
     */
    List<OrderResponseCollectorShipperDto> assignOrdersToShippers(UUID hubId, List<OrderRequestDto> orders, List<EmployeeDTO> shippers);
    /**
     * Get all current assignments for a specific shipper
     */
    List<OrderSummaryDTO> getShipperAssignments(UUID shipperId);

    /**
     * Update the status of a shipper assignment
     */
    void updateAssignmentStatus(UUID assignmentId, ShipperAssignmentStatus status);
}