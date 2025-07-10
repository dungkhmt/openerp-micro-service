package openerp.openerpresourceserver.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.dto.request.Item;
import openerp.openerpresourceserver.dto.request.RoutingRequest;
import openerp.openerpresourceserver.dto.response.RoutingResponse;
import openerp.openerpresourceserver.dto.response.TruckDTO;
import openerp.openerpresourceserver.entity.AddressType;

class Truck {
	UUID id, warehouseId;
	double maxWeight, currentWeight = 0, routeDistance = 0;
	List<UUID> route = new ArrayList<>();
	List<Item> loadedItems = new ArrayList<>();
	Map<UUID, Integer> addressToIndexMap = new HashMap<>();
	
	public Truck(UUID id, UUID warehouse, double maxWeight) {
		this.id = id;
		this.warehouseId = warehouse;
		this.maxWeight = maxWeight;
	}

	public boolean canLoad(Item item) {
		return currentWeight + item.getWeight() <= maxWeight;
	}

	public void insertItem(Item item, int position) {
		loadedItems.add(item);
		currentWeight += item.getWeight();
		if (!route.contains(item.getCustomerAddressId())) {
			route.add(position, item.getCustomerAddressId());
		}
	}
}

@Service
public class GreedyInsertionService {

	@Autowired
	AddressDistanceService addressDistanceService;

	@Autowired
	AssignedOrderItemService assignedOrderItemService;

	public RoutingResponse greedyInsert(RoutingRequest request) {

		Map<Pair<UUID, UUID>, Double> x = addressDistanceService.getDistance(AddressType.CUSTOMER,
				AddressType.CUSTOMER);
		Map<Pair<UUID, UUID>, Double> y = addressDistanceService.getDistance(AddressType.WAREHOUSE,
				AddressType.CUSTOMER);
		Map<Pair<UUID, UUID>, Double> z = addressDistanceService.getDistance(AddressType.CUSTOMER,
				AddressType.WAREHOUSE);
		List<Item> items = assignedOrderItemService.getItems();
		List<Truck> trucks = request.getVehicles().stream()
				.map(v -> new Truck(v.getVehicleId(), v.getWarehouseId(), v.getMaxWeight()))
				.collect(Collectors.toList());

		for (Item item : items) {
			int bestTruckIdx = -1;
			int bestPos = -1;
			double bestDelta = Double.POSITIVE_INFINITY;

			for (int i = 0; i < trucks.size(); i++) {
				Truck truck = trucks.get(i);
				if (!truck.warehouseId.equals(item.getWarehouseId()) || !truck.canLoad(item))
					continue;
				List<UUID> route = truck.route;
				for (int pos = 0; pos <= route.size(); pos++) {
					UUID prev = (pos == 0) ? truck.warehouseId : route.get(pos - 1);
					UUID next = (pos == route.size()) ? truck.warehouseId : route.get(pos);
					double delta = 0.0;

					if (pos == 0) {
						if (next.equals(truck.warehouseId)) {
							double d1 = y.get(Pair.of(truck.warehouseId, item.getCustomerAddressId()));
							double d2 = z.get(Pair.of(item.getCustomerAddressId(), truck.warehouseId));
							delta = d1 + d2;
						} else {
							double d1 = y.get(Pair.of(truck.warehouseId, item.getCustomerAddressId()));
							double d2 = item.getCustomerAddressId().equals(next) ? 0.0
									: x.get(Pair.of(item.getCustomerAddressId(), next));
							double d3 = y.get(Pair.of(truck.warehouseId, next));
							delta = d1 + d2 - d3;
						}
					} else if (pos == route.size()) {
						double d1 = prev.equals(item.getCustomerAddressId()) ? 0.0
								: x.get(Pair.of(prev, item.getCustomerAddressId()));
						double d2 = z.get(Pair.of(item.getCustomerAddressId(), truck.warehouseId));
						double d3 = z.get(Pair.of(prev, truck.warehouseId));
						delta = d1 + d2 - d3;
					} else {
						double d1 = prev.equals(item.getCustomerAddressId()) ? 0.0
								: x.get(Pair.of(prev, item.getCustomerAddressId()));
						double d2 = item.getCustomerAddressId().equals(next) ? 0.0
								: x.get(Pair.of(item.getCustomerAddressId(), next));
						double d3 = prev.equals(next) ? 0.0 : x.get(Pair.of(prev, next));
						delta = d1 + d2 - d3;
					}
					if (delta < bestDelta) {
						bestDelta = delta;
						bestTruckIdx = i;
						bestPos = pos;
					}
				}
			}

			if (bestTruckIdx != -1) {
				trucks.get(bestTruckIdx).insertItem(item, bestPos);
			}
		}

		

		for (Truck truck : trucks) {

			truck.route.add(0, truck.warehouseId);
			truck.route.add(truck.warehouseId);

			truck.routeDistance = 0;
			List<UUID> r = truck.route;
			if (r.size() == 2)
				continue;
			for (int i = 1; i < r.size(); i++) {
				UUID from = r.get(i - 1);
				UUID to = r.get(i);
				if (i == 1) {
					truck.routeDistance += y.get(Pair.of(truck.warehouseId, to));
					truck.addressToIndexMap.put(to, i);
				} else if (i == r.size() - 1) {
					truck.routeDistance += z.get(Pair.of(from, truck.warehouseId));
				} else {
					truck.routeDistance += x.get(Pair.of(from, to));
					truck.addressToIndexMap.put(to, i);
				}
			}

		}

		List<TruckDTO> truckDTOs = new ArrayList<>();
		double totalDistance = 0;

		for (Truck truck : trucks) {

			if (truck.routeDistance == 0)
				continue;
			List<UUID> loadedItemUUIDs = truck.loadedItems.stream().map(Item::getItemId).collect(Collectors.toList());
			List<Integer> itemSequences = truck.loadedItems.stream()
					.map(item -> truck.addressToIndexMap.get(item.getCustomerAddressId())).collect(Collectors.toList());
			TruckDTO truckDTO = TruckDTO.builder().vehicleId(truck.id).warehouse(truck.warehouseId)
					.totalWeight(truck.currentWeight).maxWeight(truck.maxWeight).route(truck.route)
					.loadedItems(loadedItemUUIDs).itemSequences(itemSequences).routeDistance(truck.routeDistance)
					.build();
			truckDTOs.add(truckDTO);
			totalDistance += truck.routeDistance;
		}

		return RoutingResponse.builder().totalDistance(totalDistance).trucks(truckDTOs).build();

	}

}