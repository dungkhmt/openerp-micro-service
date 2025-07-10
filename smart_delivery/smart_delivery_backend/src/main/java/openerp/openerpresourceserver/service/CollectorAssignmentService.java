package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.*;
import openerp.openerpresourceserver.entity.enumentity.CollectorAssignmentStatus;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

public interface CollectorAssignmentService {

    /**
     * Tạo đề xuất phân công mà không lưu vào database
     */
    List<OrderResponseCollectorShipperDto> suggestOrderToCollectorAssignment(
            Principal principal,
            UUID hubId,
            List<OrderRequestDto> orders,
            List<EmployeeDTO> collectors
    );

    /**
     * Xác nhận và lưu phân công vào database
     */
    List<OrderResponseCollectorShipperDto> confirmOrderToCollectorAssignment(
            Principal principal,
            UUID hubId,
            List<ConfirmAssignmentDto.AssignmentDetailDto> assignments
    );
    List<TodayAssignmentDto> getAssignmentTodayByHubId(Principal principal,UUID hubId);
    List<AssignOrderCollectorDTO> getAssignmentTodayByCollectorId(Principal principal, UUID collectorId);

    void updateAssignmentStatus(Principal principal, UUID assignmentId, CollectorAssignmentStatus status);

}
