package openerp.openerpresourceserver.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.dto.request.UpdateDistanceRequest;
import openerp.openerpresourceserver.entity.AddressDistance;
import openerp.openerpresourceserver.entity.AddressType;
import openerp.openerpresourceserver.projection.AddressDistanceProjection;
import openerp.openerpresourceserver.repository.AddressDistanceRepository;

@Service
public class AddressDistanceService {

	@Autowired
	private AddressDistanceRepository addressDistanceRepository;

	public Page<AddressDistanceProjection> getAllDistances(AddressType fromType, AddressType toType,
			String fromLocation, String toLocation, Pageable pageable) {
		switch (fromType) {
		case WAREHOUSE:
			if (toType == AddressType.CUSTOMER) {
				return addressDistanceRepository.findWarehouseToCustomer(fromLocation, toLocation, pageable);
			}
			break;
		case CUSTOMER:
			switch (toType) {
			case WAREHOUSE:
				return addressDistanceRepository.findCustomerToWarehouse(fromLocation, toLocation, pageable);
			case CUSTOMER:
				return addressDistanceRepository.findCustomerToCustomer(fromLocation, toLocation, pageable);
			}
			break;
		}

		throw new IllegalArgumentException("Unsupported combination: " + fromType + " -> " + toType);
	}

	public boolean updateAddressDistance(UpdateDistanceRequest request) {
		Optional<AddressDistance> optionalDistance = addressDistanceRepository.findById(request.getAddressDistanceId());
		if (optionalDistance.isPresent()) {
			AddressDistance existingDistance = optionalDistance.get();
			existingDistance.setDistance(request.getDistance());
			existingDistance.setLastUpdatedStamp(LocalDateTime.now());
			addressDistanceRepository.save(existingDistance);
			return true;
		}
		return false;
	}

}
