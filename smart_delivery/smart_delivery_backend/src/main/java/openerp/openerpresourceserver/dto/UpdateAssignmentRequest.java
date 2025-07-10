package openerp.openerpresourceserver.dto;

import lombok.Data;
import openerp.openerpresourceserver.entity.enumentity.CollectorAssignmentStatus;

import java.util.UUID;

@Data
public class UpdateAssignmentRequest {
        private UUID assignmentId;
        private CollectorAssignmentStatus status;

        // Getters và Setters

}
