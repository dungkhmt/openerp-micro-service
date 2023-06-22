package openerp.containertransport.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import openerp.containertransport.constants.Constants;
import openerp.containertransport.dto.TripFilterRequestDTO;
import openerp.containertransport.dto.TripItemModel;
import openerp.containertransport.dto.TripModel;
import openerp.containertransport.entity.Order;
import openerp.containertransport.entity.Shipment;
import openerp.containertransport.entity.Trip;
import openerp.containertransport.entity.Truck;
import openerp.containertransport.repo.OrderRepo;
import openerp.containertransport.repo.ShipmentRepo;
import openerp.containertransport.repo.TripRepo;
import openerp.containertransport.repo.TruckRepo;
import openerp.containertransport.service.TripItemService;
import openerp.containertransport.service.TripService;
import openerp.containertransport.utils.RandomUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {
    private final TripRepo tripRepo;
    private final TripItemService tripItemService;
    private final TruckRepo truckRepo;
    private final ModelMapper modelMapper;
    private final OrderRepo orderRepo;
    private final ShipmentRepo shipmentRepo;
    private final EntityManager entityManager;
    @Override
    public TripModel createTrip(TripModel tripModel, String shipmentId, String createBy) {
        Trip trip = new Trip();
        Truck truck = truckRepo.findById(tripModel.getTruckId()).get();
        truck.setStatus(Constants.TruckStatus.SCHEDULED.getStatus());
        truckRepo.save(truck);
        List<Order> orders = new ArrayList<>();
        tripModel.getOrderIds().forEach((item) -> {
            Order order = orderRepo.findById(item).get();
            order.setStatus("SCHEDULED");
            order = orderRepo.save(order);
            orders.add(order);
        });
        Shipment shipment = shipmentRepo.findByUid(shipmentId);
        trip.setShipment(shipment);
        trip.setTruck(truck);
        trip.setDriverId(truck.getDriverId());
        trip.setStatus("Waiting");
        trip.setUid(RandomUtils.getRandomId());
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
                TripItemModel tripItemModel = tripItemService.createTripItem(item, finalTrip.getUid());
                tripItemModels.add(tripItemModel);
            });
        }
        tripModelCreate.setTripItemModelList(tripItemModels);
        tripModelCreate.setOrderIds(tripModel.getOrderIds());

        return tripModelCreate;
    }

    @Override
    public List<TripModel> filterTrip(TripFilterRequestDTO requestDTO) {
        String sql = "SELECT * FROM container_transport_trip WHERE 1=1";
        HashMap<String, Object> params = new HashMap<>();

        sql += " AND shipment_id = :shipmentId";
        params.put("shipmentId", requestDTO.getShipmentId());

        if (requestDTO.getStatus() != null) {
            sql += " AND status = :status";
            params.put("status", requestDTO.getStatus());
        }

        sql += " ORDER BY updated_at DESC";
        Query query = this.entityManager.createNativeQuery(sql, Trip.class);
        for (String i : params.keySet()) {
            query.setParameter(i, params.get(i));
        }
        List<Trip> trips = query.getResultList();
        List<TripModel> tripModels = new ArrayList<>();
        trips.forEach((item) -> tripModels.add(convertToModel(item)));
        return tripModels;
    }

    @Override
    public TripModel getByUid(String uid) {
        Trip trip = tripRepo.findByUid(uid);
        return convertToModel(trip);
    }

    @Override
    public List<TripModel> getTripsByDriver(TripFilterRequestDTO requestDTO) {
        String sql = "SELECT * FROM container_transport_trip WHERE 1=1";
        HashMap<String, Object> params = new HashMap<>();

        sql += " AND driver_id = :driverId";
        params.put("driverId", requestDTO.getUsername());

        if (requestDTO.getStatus() != null) {
            if(requestDTO.getStatus().equals("Pending")) {
                List<String> status = new ArrayList<>();
                status.add("SCHEDULED");
                status.add("EXECUTING");
                sql += " AND status in :status";
                params.put("status", status);
            }
            else {
                sql += " AND status = :status";
                params.put("status", requestDTO.getStatus());
            }
        }

        sql += " ORDER BY updated_at DESC";
        Query query = this.entityManager.createNativeQuery(sql, Trip.class);
        for (String i : params.keySet()) {
            query.setParameter(i, params.get(i));
        }
        List<Trip> trips = query.getResultList();
        List<TripModel> tripModels = new ArrayList<>();
        trips.forEach((item) -> tripModels.add(convertToModel(item)));
        return tripModels;
    }

    @Override
    public TripModel updateTrip(String uid, TripModel tripModel) {
        Trip trip = tripRepo.findByUid(uid);
        if(tripModel.getStatus() != null) {
            trip.setStatus(tripModel.getStatus());
        }
        trip = tripRepo.save(trip);
        return convertToModel(trip);
    }

    public TripModel convertToModel(Trip trip) {
        TripModel tripModel = modelMapper.map(trip, TripModel.class);
        List<Long> orderIds = new ArrayList<>();
        tripModel.setTruckId(trip.getTruck().getId());
        tripModel.setDriverName(trip.getTruck().getDriverName());
        tripModel.setTruckCode(trip.getTruck().getTruckCode());
        trip.getOrders().forEach((item) -> orderIds.add(item.getId()));
        tripModel.setOrderIds(orderIds);
        tripModel.setOrders(trip.getOrders());
        return tripModel;
    }
}
