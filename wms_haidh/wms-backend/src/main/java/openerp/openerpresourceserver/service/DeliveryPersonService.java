package openerp.openerpresourceserver.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.entity.DeliveryPerson;
import openerp.openerpresourceserver.entity.projection.DeliveryPersonProjection;
import openerp.openerpresourceserver.repository.DeliveryPersonRepository;

@Service
public class DeliveryPersonService {

	private final DeliveryPersonRepository deliveryPersonRepository;

	public DeliveryPersonService(DeliveryPersonRepository deliveryPersonRepository) {
		this.deliveryPersonRepository = deliveryPersonRepository;
	}

	public List<DeliveryPersonProjection> getAllDeliveryPersons() {
		return deliveryPersonRepository.findAllDeliveryPersons();
	}

	public Page<DeliveryPerson> getAllDeliveryPersons(int page, int size, String search) {
		Pageable pageable = PageRequest.of(page, size);
		if (search != null && !search.trim().isEmpty()) {
			return deliveryPersonRepository.findByFullNameContainingIgnoreCase(search, pageable);
		}
		return deliveryPersonRepository.findAll(pageable);
	}
}
