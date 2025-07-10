package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.dto.VehicleDto;

import java.util.List;
import java.util.UUID;

public interface VehicleCustomRepository{
    List<VehicleDto> getVehicleByHubId(UUID hubId);
}
