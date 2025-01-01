package openerp.openerpresourceserver.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.entity.projection.BayProjection;
import openerp.openerpresourceserver.repository.BayRepository;

@Service
public class BayService {

	@Autowired
    private  BayRepository bayRepository;

	public List<BayProjection> getBaysByWarehouseId(UUID warehouseId) {
        return bayRepository.findByWarehouseIdWithProjection(warehouseId);
    }
}
