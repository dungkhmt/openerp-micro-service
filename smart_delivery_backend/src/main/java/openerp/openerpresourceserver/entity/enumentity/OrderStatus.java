package openerp.openerpresourceserver.entity.enumentity;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Getter
public enum OrderStatus {

    PENDING("Đang xử lý", Collections.emptySet()),
    PROCESSING("Đang xử lý", Collections.emptySet()),
    ASSIGNED("Đã phân công cho nhân viên thu gom", Set.of(Role.ADMIN, Role.HUB_MANAGER, Role.COLLECTOR)),
    COLLECTED_COLLECTOR("Nhân viên thu gom đã lấy hàng", Set.of(Role.COLLECTOR)),
    COLLECTED_HUB("Đơn hàng đã về Hub nguồn", Set.of(Role.HUB_STAFF)),
    DELIVERING("Đang vận chuyển", Set.of(Role.ADMIN, Role.HUB_STAFF)),
    DELIVERED("Đã vận chuyển đến Hub đích", Set.of(Role.ADMIN, Role.HUB_STAFF)),
    ASSIGNED_SHIPPER("Đã phân công cho shipper", Set.of(Role.ADMIN, Role.HUB_MANAGER)),
    SHIPPED("Đã giao hàng", Set.of(Role.ADMIN, Role.SHIPPER)),
    COMPLETED("Đã hoàn thành", Set.of(Role.ADMIN)),
    CANCELLED("Đã hủy", Set.of(Role.ADMIN, Role.CUSTOMER)),
    DELETED("Đã hủy", Set.of(Role.ADMIN, Role.CUSTOMER, Role.HUB_MANAGER));
    private final String description;
    private final Set<Role> rolesAllowedToUpdate;

    OrderStatus(String description, Set<Role> rolesAllowedToUpdate) {
        this.description = description;
        this.rolesAllowedToUpdate = rolesAllowedToUpdate;
    }

    // Định nghĩa các chuyển đổi trạng thái hợp lệ
    public static List<OrderStatus> getAllowedNextStatuses(OrderStatus currentStatus) {
        switch (currentStatus) {
            case PENDING:
                return Arrays.asList(ASSIGNED, CANCELLED);
            case ASSIGNED:
                return Arrays.asList(COLLECTED_COLLECTOR, CANCELLED);
            case COLLECTED_COLLECTOR:
                return Arrays.asList(COLLECTED_HUB);
            case COLLECTED_HUB:
                return Arrays.asList(DELIVERING, CANCELLED);
            case DELIVERING:
                return Arrays.asList(DELIVERED, CANCELLED);
            case DELIVERED:
                return Arrays.asList(ASSIGNED_SHIPPER, CANCELLED);
            case ASSIGNED_SHIPPER:
                return Arrays.asList(SHIPPED, CANCELLED);
            case SHIPPED:
                return Arrays.asList(COMPLETED, CANCELLED);
            case COMPLETED:
            case CANCELLED:
                return Collections.emptyList(); // Trạng thái cuối cùng
            default:
                return Collections.emptyList();
        }
    }

    // Kiểm tra xem chuyển đổi trạng thái có hợp lệ không
    public static boolean isValidTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        return getAllowedNextStatuses(currentStatus).contains(newStatus);
    }
    // Get the full lifecycle of a package
    public static List<OrderStatus> getFullLifecycle() {
        return Arrays.asList(
                PENDING,           // Initial order created
                ASSIGNED,          // Assigned to collector
                COLLECTED_COLLECTOR, // Picked up by collector
                COLLECTED_HUB,     // Arrived at origin hub
                DELIVERING,        // In transit between hubs
                DELIVERED,         // Arrived at destination hub
                ASSIGNED_SHIPPER,  // Assigned to shipper for final delivery
                SHIPPED,           // Out for delivery to recipient
                COMPLETED          // Successfully delivered to recipient
        );
    }

    // Get the third mile statuses
    public static List<OrderStatus> getThirdMileStatuses() {
        return Arrays.asList(
                DELIVERED,         // Ready at destination hub
                ASSIGNED_SHIPPER,  // Assigned to shipper
                SHIPPED,           // Out for delivery
                COMPLETED          // Successfully delivered
        );
    }
}

