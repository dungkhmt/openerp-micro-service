package openerp.containertransport.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import openerp.containertransport.algorithms.entity.DistanceElement;
import openerp.containertransport.algorithms.entity.DistantKey;
import openerp.containertransport.constants.Constants;
import openerp.containertransport.dto.*;
import openerp.containertransport.dto.validDTO.ValidTripItemDTO;
import openerp.containertransport.entity.*;
import openerp.containertransport.repo.*;
import openerp.containertransport.service.FacilityService;
import openerp.containertransport.service.RelationshipService;
import openerp.containertransport.service.TripItemService;
import openerp.containertransport.service.TripService;
import openerp.containertransport.utils.RandomUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    private final RelationshipService relationshipService;
    private final FacilityService facilityService;
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

    @Override
    public List<Trip> getTripByShipmentId(String shipmentId) {
        List<Trip> tripList = tripRepo.getTripByShipmentId(shipmentId);
        return tripList;
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

    public ValidTripItemDTO checkValidTrip(List<TripItemModel> tripItemModels, long startTime) {
        ValidTripItemDTO validTripItemDTO = new ValidTripItemDTO();

        List<Relationship> relationships = relationshipService.getAllRelationShip();
        Map<DistantKey, DistanceElement> distanceElementMap = convertToDistantInput(relationships);

        FacilityFilterRequestDTO facilityFilter = new FacilityFilterRequestDTO();
        List<FacilityModel> facilityModels = facilityService.filterFacility(facilityFilter).getFacilityModels();
        Map<Long, FacilityModel> facilityModelMap = convertToMapFacility(facilityModels);

        Long totalTime = startTime;
        BigDecimal totalDistant = new BigDecimal(0);
        Integer prevPick = tripItemModels.get(0).getFacilityId().intValue();
        for (int i = 1; i < tripItemModels.size(); i++) {
            DistantKey distantKey = DistantKey.builder()
                    .fromFacility(prevPick)
                    .toFacility(tripItemModels.get(i).getFacilityId().intValue())
                    .build();
            Long time = distanceElementMap.get(distantKey).getTravelTime();
            BigDecimal distant = distanceElementMap.get(distantKey).getDistance();

            totalTime += time;
            totalDistant = totalDistant.add(distant);

            if(tripItemModels.get(i).getAction().equals("PICKUP_CONTAINER") && !tripItemModels.get(i).getTypeOrder().equals("OE")
                    && totalTime > tripItemModels.get(i).getLateArrivalTime()) {
                validTripItemDTO.setCheck(false);
                validTripItemDTO.setMessageErr("Trip is incorrect time when PICKUP_CONTAINER of "+ tripItemModels.get(i).getOrderCode());
                return validTripItemDTO;
            }

            if(tripItemModels.get(i).getAction().equals("DELIVERY_CONTAINER") && !tripItemModels.get(i).getTypeOrder().equals("IE")
                    && totalTime > tripItemModels.get(i).getLateDepartureTime()) {
                validTripItemDTO.setCheck(false);
                validTripItemDTO.setMessageErr("Trip is incorrect time when DELIVERY_CONTAINER of "+ tripItemModels.get(i).getOrderCode());
                return validTripItemDTO;
            }

            if(tripItemModels.get(i-1).getAction().equals("PICKUP_CONTAINER")){
                totalTime += facilityModelMap.get(tripItemModels.get(i).getFacilityId()).getProcessingTimePickUp();
            }

            if(tripItemModels.get(i-1).getAction().equals("DELIVERY_CONTAINER")){
                totalTime += facilityModelMap.get(tripItemModels.get(i).getFacilityId()).getProcessingTimeDrop();
            }
            prevPick = tripItemModels.get(i).getFacilityId().intValue();
        }
        validTripItemDTO.setTotalDistant(totalDistant);
        validTripItemDTO.setTotalTime(totalTime);
        validTripItemDTO.setCheck(true);

        return validTripItemDTO;
    }

    public Map<DistantKey, DistanceElement> convertToDistantInput(List<Relationship> relationships) {
        Map<DistantKey, DistanceElement> distanceMap = new HashMap<>();
        relationships.forEach(relationship -> {
            DistanceElement distanceElement = new DistanceElement();
            distanceElement.setFromFacility((int) relationship.getFromFacility().longValue());
            distanceElement.setToFacility((int) relationship.getToFacility().longValue());
            distanceElement.setDistance(relationship.getDistant());
            distanceElement.setTravelTime(relationship.getTime());

            DistantKey distantKey = DistantKey.builder()
                    .fromFacility((int) relationship.getFromFacility().longValue())
                    .toFacility((int) relationship.getToFacility().longValue())
                    .build();
            distanceMap.put(distantKey, distanceElement);
        });
        return distanceMap;
    }

    public Map<Long, FacilityModel> convertToMapFacility(List<FacilityModel> facilityModels) {
        Map<Long, FacilityModel> facilityModelMap = new HashMap<>();
        for (FacilityModel facilityModel : facilityModels) {
            facilityModelMap.put(facilityModel.getId(), facilityModel);
        }
        return facilityModelMap;
    }
}
