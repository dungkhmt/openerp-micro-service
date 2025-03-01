package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.AssignOrderCollectorDTO;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.enumentity.CollectorAssignmentStatus;

import java.util.List;
import java.util.UUID;

public interface AssignmentService {
    void assignOrderToHub(Order order);


    void updateAssignmentStatus(UUID assignmentId, CollectorAssignmentStatus status);
}
