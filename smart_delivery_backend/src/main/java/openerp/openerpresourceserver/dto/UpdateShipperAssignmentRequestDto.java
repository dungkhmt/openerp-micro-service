package openerp.openerpresourceserver.dto;

import lombok.Data;
import openerp.openerpresourceserver.entity.enumentity.ShipperAssignmentStatus;

import java.util.UUID;

@Data
public class UpdateShipperAssignmentRequestDto {
    private UUID assignmentId;
    private ShipperAssignmentStatus status;
}