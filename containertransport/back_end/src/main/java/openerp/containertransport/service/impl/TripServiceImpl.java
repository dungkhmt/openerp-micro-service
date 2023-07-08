package openerp.containertransport.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import openerp.containertransport.constants.Constants;
import openerp.containertransport.dto.TripDeleteDTO;
import openerp.containertransport.dto.TripFilterRequestDTO;
import openerp.containertransport.dto.TripItemModel;
import openerp.containertransport.dto.TripModel;
import openerp.containertransport.entity.*;
import openerp.containertransport.repo.*;
import openerp.containertransport.service.TripItemService;
import openerp.containertransport.service.TripService;
import openerp.containertransport.utils.RandomUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {
    private final TripRepo tripRepo;
    private final TripItemService tripItemService;
    private final TripItemRepo tripItemRepo;
    private final TruckRepo truckRepo;
    private final TrailerRepo trailerRepo;
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
        trip.setStatus("SCHEDULED");
        trip.setUid(RandomUtils.getRandomId());
        trip.setCreatedByUserId(createBy);
        trip.setOrders(orders);
        trip.setTotalDistant(tripModel.getTotalDistant());
        trip.setTotalTime(tripModel.getTotalTime());
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
        List<TripItemModel> tripItemModelsOld = tripItemService.getTripItemByTripId(trip.getUid());

        if(tripModel.getStatus() != null) {
            trip.setStatus(tripModel.getStatus());
        }
        if(tripModel.getTruckId() != null && tripModel.getTruckId() != trip.getTruck().getId()) {
            Truck truckOld = truckRepo.findByUid(trip.getTruck().getUid());
            truckOld.setStatus(Constants.TruckStatus.AVAILABLE.getStatus());
            truckRepo.save(truckOld);

            Truck truckUpdate = truckRepo.findByUid(tripModel.getTruckUid());
            truckUpdate.setStatus(Constants.TruckStatus.SCHEDULED.getStatus());
            truckRepo.save(truckUpdate);
            trip.setTruck(truckUpdate);
            trip.setDriverId(truckUpdate.getDriverId());
        }
        if(tripModel.getOrderIds().size() != trip.getOrders().size()
                || !Arrays.deepEquals(tripModel.getOrderIds().toArray(new Long[0]), trip.getOrders().stream().map(Order::getId).toArray())
                || !checkSameTripItem(tripItemModelsOld, tripModel.getTripItemModelList())) {

            // update status oldOrder
            List<Order> ordersOld = trip.getOrders();
            ordersOld.forEach((order) -> {
                Order orderOld = orderRepo.findByUid(order.getUid());
                orderOld.setStatus(Constants.OrderStatus.ORDERED.getStatus());
                orderRepo.save(orderOld);
            });

            // update status OrderUpdate
            List<Order> ordersUpdate = new ArrayList<>();
            tripModel.getOrderIds().forEach((orderId) -> {
                Order order = orderRepo.findById(orderId).get();
                order.setStatus("SCHEDULED");
                order = orderRepo.save(order);
                ordersUpdate.add(order);
            });
            trip.setOrders(ordersUpdate);

            // delete old tripItem
            tripItemRepo.deleteByTripUid(trip.getUid());

            // create new tripItem
            Trip finalTrip = trip;
            tripModel.getTripItemModelList().forEach((item) -> {
                TripItemModel tripItemModel = tripItemService.createTripItem(item, finalTrip.getUid());
            });

        }
        trip = tripRepo.save(trip);
        return convertToModel(trip);
    }

    @Override
    public List<TripModel> deleteTrips(TripDeleteDTO tripDeleteDTO) {
        List<TripModel> tripModels = new ArrayList<>();
        List<String> uidList = tripDeleteDTO.getListUidTrip();
        uidList.forEach((tripUid) -> {
            Trip trip = tripRepo.findByUid(tripUid);
            if(trip.getStatus().equals(Constants.TripStatus.SCHEDULED.getStatus())
//                    && trip.getShipment().getStatus().equals(Constants.ShipmentStatus.SCHEDULED.getStatus())
            ) {
                tripItemRepo.deleteByTripUid(tripUid);
                List<Order> orders = trip.getOrders();
                orders.forEach((order) -> {
                    order.setStatus(Constants.OrderStatus.ORDERED.getStatus());
                    orderRepo.save(order);
                });

                Truck truck = truckRepo.findByUid(trip.getTruck().getUid());
                truck.setStatus(Constants.TruckStatus.AVAILABLE.getStatus());
                truckRepo.save(truck);

                List<TripItem> tripItems = tripItemRepo.findByTripId(tripUid);
                tripItems.forEach((item) -> {
                    if(item.getTrailer() != null) {
                        Trailer trailer = item.getTrailer();
                        trailer.setStatus(Constants.TrailerStatus.AVAILABLE.getStatus());
                        trailerRepo.save(trailer);
                    }
                });

                Trip tripDelete = tripRepo.deleteTripByUid(tripUid);
                tripModels.add(convertToModel(tripDelete));
            }
        });
        return tripModels;
    }

    public TripModel convertToModel(Trip trip) {
        TripModel tripModel = modelMapper.map(trip, TripModel.class);
        List<Long> orderIds = new ArrayList<>();
        tripModel.setTruckId(trip.getTruck().getId());
        tripModel.setTruckUid(trip.getTruck().getUid());
        tripModel.setDriverName(trip.getTruck().getDriverName());
        tripModel.setTruckCode(trip.getTruck().getTruckCode());
        trip.getOrders().forEach((item) -> orderIds.add(item.getId()));
        tripModel.setOrderIds(orderIds);
        tripModel.setOrders(trip.getOrders());
        return tripModel;
    }

    public boolean checkSameTripItem(List<TripItemModel> tripItemModelsOld, List<TripItemModel> tripItemModelsNew) {
        if(tripItemModelsOld.size() != tripItemModelsNew.size()) {
            return false;
        }
        for (int i = 0; i < tripItemModelsOld.size(); i++){
            if(!Objects.equals(tripItemModelsOld.get(i).getAction(), tripItemModelsNew.get(i).getAction())
            || tripItemModelsOld.get(i).getOrderCode() != tripItemModelsNew.get(i).getOrderCode()) {
                return false;
            }
        }
        return true;
    }
}
