package openerp.openerpresourceserver.controller;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.*;
import openerp.openerpresourceserver.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/smdeli/assignment/collector")
public class CollectorAssignmentController {
    private final CollectorAssignmentService collectorAssignmentService;
    /**
     * API để lấy đề xuất phân công (không lưu vào database)
     */
    @PostMapping("/order/suggest/collector")
    public ResponseEntity<List<OrderResponseCollectorShipperDto>> suggestOrderAssignment(@RequestBody AssignOrderDto request, Principal principal) {
        try {
            // Gọi service để tạo đề xuất phân công
            List<OrderResponseCollectorShipperDto> suggestions = collectorAssignmentService.suggestOrderToCollectorAssignment(
                    principal,
                    request.getHubId(),
                    request.getOrders(),
                    request.getEmployees()
            );
            return ResponseEntity.ok(suggestions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    /**
     * API để xác nhận phân công (lưu vào database)
     */
    @PostMapping("/order/confirm/collector")
    public ResponseEntity<List<OrderResponseCollectorShipperDto>> confirmOrderAssignment(
            Principal principal,
            @RequestBody ConfirmAssignmentDto request) {
        try {
            List<OrderResponseCollectorShipperDto> result = collectorAssignmentService.confirmOrderToCollectorAssignment(
                    principal,
                    request.getHubId(),
                    request.getAssignments()
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    // Get
    @GetMapping("/order/assign/collector/today/{hubId}")
    public ResponseEntity<List<TodayAssignmentDto>> getAssignmentTodayByHubId(Principal principal, @PathVariable UUID hubId) {
        return ResponseEntity.ok(collectorAssignmentService.getAssignmentTodayByHubId(principal, hubId));
    }
    @GetMapping("/order/assign/today/collector/{collectorId}")
    public ResponseEntity<List<AssignOrderCollectorDTO>> getAssignmentTodayByCollectorId(Principal principal,@PathVariable UUID collectorId) {
        return ResponseEntity.ok(collectorAssignmentService.getAssignmentTodayByCollectorId(principal, collectorId));
    }
    @PutMapping("/order/assignment/collector")
    public ResponseEntity<?> updateAssignment(@RequestBody UpdateAssignmentRequest request, Principal principal) {
        try {
            collectorAssignmentService.updateAssignmentStatus(principal, request.getAssignmentId(), request.getStatus());
            return ResponseEntity.ok("Assignment updated successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }


}
