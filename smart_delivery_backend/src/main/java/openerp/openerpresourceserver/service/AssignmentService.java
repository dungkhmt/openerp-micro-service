package openerp.openerpresourceserver.service;

import jakarta.transaction.Transactional;
import openerp.openerpresourceserver.dto.AssignOrderCollectorDTO;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.enumentity.CollectorAssignmentStatus;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.UUID;

public interface AssignmentService {
    void assignOrderToHub(Order order);
    void updateAssignmentStatus(UUID assignmentId, CollectorAssignmentStatus status);

    @Transactional
    void decreaseVehicleLoad(UUID vehicleId, UUID orderId);

    @Transactional
    void assignOrderItemsToTrip(UUID hubId);

    @Scheduled(cron = "0 0 6 * * ?")  // Runs every day at 6:00 AM
    void assignOrderItemsForTripsForAllHubs();
}
