package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.*;
import openerp.openerpresourceserver.entity.enumentity.ShipperAssignmentStatus;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

public interface ShipperAssignmentService {
    /**
     * Tạo đề xuất phân công shipper mà không lưu vào database
     */
    List<OrderResponseCollectorShipperDto> suggestOrdersToShippers(
            UUID hubId,
            List<OrderRequestDto> orders,
            List<EmployeeDTO> shippers
    );

    /**
     * Xác nhận và lưu phân công shipper vào database
     */
    List<OrderResponseCollectorShipperDto> confirmOrdersToShippers(
            Principal principal,
            UUID hubId,
            List<ConfirmAssignmentDto.AssignmentDetailDto> assignments
    );

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
    void updateAssignmentStatus(Principal principal, UUID assignmentId, ShipperAssignmentStatus status);

    List<TodayAssignmentShipperDto> getShipperAssignmentsTodayByHub(UUID hubId);

    List<AssignOrderShipperDto> getShipperAssignmentsToday(UUID shipperId);
}