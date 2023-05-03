package openerp.containertransport.service.impl;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.dto.TripItemModel;
import openerp.containertransport.dto.TripModel;
import openerp.containertransport.entity.Order;
import openerp.containertransport.entity.Trip;
import openerp.containertransport.entity.Truck;
import openerp.containertransport.repo.OrderRepo;
import openerp.containertransport.repo.TripRepo;
import openerp.containertransport.repo.TruckRepo;
import openerp.containertransport.service.TripItemService;
import openerp.containertransport.service.TripService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {
    private final TripRepo tripRepo;
    private final TripItemService tripItemService;
    private final TruckRepo truckRepo;
    private final ModelMapper modelMapper;
    private final OrderRepo orderRepo;
    @Override
    public TripModel createTrip(TripModel tripModel, long shipmentId, String createBy) {
        Trip trip = new Trip();
        Truck truck = truckRepo.findById(tripModel.getTruckId()).get();
        List<Order> orders = new ArrayList<>();
        tripModel.getOrderIds().forEach((item) -> {
            orders.add(orderRepo.findById(item).get());
        });
        trip.setShipmentId(shipmentId);
        trip.setTruck(truck);
        trip.setStatus("Waiting");
        trip.setCreatedByUserId(createBy);
        trip.setOrders(orders);
        trip.setCreatedAt(System.currentTimeMillis());
        trip.setUpdatedAt(System.currentTimeMillis());
        trip = tripRepo.save(trip);
        trip.setCode("TRIP" + trip.getId());
        trip = tripRepo.save(trip);

        TripModel tripModelCreate = convertToModel(trip);
        List<TripItemModel> tripItemModels = new ArrayList<>();
        if (!tripModel.getTripItemModelList().isEmpty()) {
            Trip finalTrip = trip;
            tripModel.getTripItemModelList().forEach((item) -> {
                TripItemModel tripItemModel = tripItemService.createTripItem(item, finalTrip.getId());
                tripItemModels.add(tripItemModel);
            });
        }
        tripModelCreate.setTripItemModelList(tripItemModels);
        tripModelCreate.setOrderIds(tripModel.getOrderIds());

        return tripModelCreate;
    }

    @Override
    public TripModel filterTrip() {
        return null;
    }

    public TripModel convertToModel(Trip trip) {
        TripModel tripModel = modelMapper.map(trip, TripModel.class);
        tripModel.setTruckId(trip.getTruck().getId());
        tripModel.setTruckName(trip.getTruck().getDriverName());
        return tripModel;
    }
}
