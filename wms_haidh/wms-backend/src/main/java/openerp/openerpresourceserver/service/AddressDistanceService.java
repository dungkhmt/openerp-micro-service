package openerp.openerpresourceserver.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.request.AddressDistanceDTO;
import openerp.openerpresourceserver.dto.request.CoordinateDTO;
import openerp.openerpresourceserver.dto.request.CoordinatePair;
import openerp.openerpresourceserver.dto.request.UpdateDistanceRequest;
import openerp.openerpresourceserver.dto.response.AddressDistanceResponse;
import openerp.openerpresourceserver.entity.AddressDistance;
import openerp.openerpresourceserver.entity.AddressType;
import openerp.openerpresourceserver.repository.AddressDistanceRepository;
import openerp.openerpresourceserver.service.route.RouteService;

@Service
@AllArgsConstructor
public class AddressDistanceService {

	private AddressDistanceRepository addressDistanceRepository;
	private RouteService routeService;

	public Page<AddressDistanceResponse> getAllDistances(AddressType fromType, AddressType toType,
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

	public void updateDistances() {
		List<AddressDistanceDTO> list = new ArrayList<>();
		list.addAll(addressDistanceRepository.findWarehouseToCustomerWithDistanceZero());
		list.addAll(addressDistanceRepository.findCustomerToWarehouseWithDistanceZero());
		list.addAll(addressDistanceRepository.findCustomerToCustomerWithDistanceZero());

		if (list.isEmpty())
			return;

		Set<CoordinateDTO> uniqueCoordinates = new HashSet<>();
		for (AddressDistanceDTO dto : list) {
			uniqueCoordinates.add(dto.getFrom());
			uniqueCoordinates.add(dto.getTo());
		}
		List<CoordinateDTO> coordinateList = new ArrayList<>(uniqueCoordinates);

		double[][] distance = routeService.getDistances(coordinateList);

		// Build map for quick lookup
		Map<CoordinatePair, UUID> coordinatePairToId = new HashMap<>();
		for (AddressDistanceDTO dto : list) {
			coordinatePairToId.put(new CoordinatePair(dto.getFrom(), dto.getTo()), dto.getAddressDistanceId());
		}

		// Iterate over upper triangle of the matrix
		UUID id;
		for (int i = 0; i < coordinateList.size(); i++) {
			for (int j = i + 1; j < coordinateList.size(); j++) {
				CoordinateDTO from = coordinateList.get(i);
				CoordinateDTO to = coordinateList.get(j);
				id = coordinatePairToId.get(new CoordinatePair(from, to));
				addressDistanceRepository.updateDistanceById(id, distance[i][j]);
				id = coordinatePairToId.get(new CoordinatePair(to, from));
				addressDistanceRepository.updateDistanceById(id, distance[j][i]);

			}
		}
	}

	public boolean updateAddressDistance(UpdateDistanceRequest request) {
		int updatedRows = addressDistanceRepository.updateDistanceById(request.getAddressDistanceId(),
				request.getDistance());
		return updatedRows > 0;
	}

	public Map<Pair<UUID, UUID>, Double> getDistance(AddressType fromType, AddressType toType) {
		List<AddressDistance> customerDistances = addressDistanceRepository
				.findByFromLocationTypeAndToLocationType(fromType, toType);
		return customerDistances.stream().collect(Collectors
				.toMap(ad -> Pair.of(ad.getFromLocationId(), ad.getToLocationId()), AddressDistance::getDistance));
	}

}
