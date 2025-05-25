package openerp.openerpresourceserver.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import openerp.openerpresourceserver.entity.Bay;
import openerp.openerpresourceserver.projection.BayProjection;
import openerp.openerpresourceserver.repository.BayRepository;

@Service
public class BayService {

	@Autowired
	private BayRepository bayRepository;

	public List<BayProjection> getBaysProjectionByWarehouseId(UUID warehouseId) {
		return bayRepository.findByWarehouseIdWithProjection(warehouseId);
	}

	public List<Bay> getBaysByWarehouseIdAndShelf(UUID warehouseId, int shelf) {
	        return bayRepository.findByWarehouseIdAndShelf(warehouseId, shelf);
	}

	public UUID getWarehouseIdByBayId(UUID bayId) {
		return bayRepository.findByBayId(bayId).map(Bay::getWarehouseId)
				.orElseThrow(() -> new IllegalArgumentException("Bay ID not found: " + bayId));
	}

	public Bay getBayById(UUID bayId) {
		return bayRepository.findById(bayId)
				.orElseThrow(() -> new EntityNotFoundException("Bay not found with ID: " + bayId));
	}
}
