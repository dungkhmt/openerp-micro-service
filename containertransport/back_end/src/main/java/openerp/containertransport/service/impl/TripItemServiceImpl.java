package openerp.containertransport.service.impl;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.constants.Constants;
import openerp.containertransport.dto.TripItemModel;
import openerp.containertransport.entity.*;
import openerp.containertransport.repo.*;
import openerp.containertransport.service.TripItemService;
import openerp.containertransport.service.TripService;
import openerp.containertransport.utils.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripItemServiceImpl implements TripItemService {
    private final TripItemRepo tripItemRepo;
    private final ModelMapper modelMapper;
    private final FacilityRepo facilityRepo;
    private final ContainerRepo containerRepo;
    private final TrailerRepo trailerRepo;
    private final TripRepo tripRepo;
    private final OrderRepo orderRepo;
    private final ShipmentRepo shipmentRepo;
    private final TruckRepo truckRepo;
    private final ContainerServiceImpl containerService;

    @Override
    public TripItemModel createTripItem(TripItemModel tripItemModel, String tripUid) {
        TripItem tripItem = new TripItem();
        Facility facility = facilityRepo.findById(tripItemModel.getFacilityId()).get();
        if(tripItemModel.getContainerId() != null) {
            Container container = containerRepo.findById(tripItemModel.getContainerId()).get();
            container.setStatus(Constants.ContainerStatus.SCHEDULED.getStatus());
            container.setUpdatedAt(System.currentTimeMillis());

            tripItem.setContainer(container);
            containerRepo.save(container);
        }
        if(tripItemModel.getTrailerId() != null) {
            Trailer trailer = trailerRepo.findByTrailerId(tripItemModel.getTrailerId());
            trailer.setStatus(Constants.TrailerStatus.SCHEDULED.getStatus());
            trailer.setUpdatedAt(System.currentTimeMillis());

            tripItem.setTrailer(trailer);
            trailerRepo.save(trailer);
        }
        if(tripItemModel.getOrderCode() != null) {
            tripItem.setOrderCode(tripItemModel.getOrderCode());
        }
        if(tripItemModel.getOrderUid() != null) {
            Order order = orderRepo.findByUid(tripItemModel.getOrderUid());
            tripItem.setOrder(order);
        }
        Trip trip = tripRepo.findByUid(tripUid);
        tripItem.setTrip(trip);
        tripItem.setSeq((int) tripItemModel.getSeq());
        tripItem.setAction(tripItemModel.getAction());
        tripItem.setFacility(facility);
        tripItem.setStatus("SCHEDULED");
        tripItem.setUid(RandomUtils.getRandomId());
        tripItem.setType(tripItemModel.getType());
        tripItem.setArrivalTime(tripItemModel.getArrivalTime());
        tripItem.setDepartureTime(tripItemModel.getDepartureTime());
        tripItem.setCreatedAt(System.currentTimeMillis());
        tripItem.setUpdatedAt(System.currentTimeMillis());
        tripItem = tripItemRepo.save(tripItem);
        tripItem.setCode("TRIPITEM" + tripItem.getId());
        tripItem = tripItemRepo.save(tripItem);
        return convertToModel(tripItem);
    }

    @Override
    public List<TripItemModel> getTripItemByTripId(String tripId) {
        List<TripItem> tripItems = tripItemRepo.findByTripId(tripId);
        Collections.sort(tripItems, (t1, t2) -> {
            return Math.toIntExact(t1.getId() - t2.getId());
        });
        List<TripItemModel> tripItemModels = new ArrayList<>();
        tripItems.forEach((tripItem -> tripItemModels.add(convertToModel(tripItem))));
        return tripItemModels;
    }

    @Override
    public TripItemModel updateTripItem(String uid, TripItemModel tripItemModel) {
        TripItem tripItem = tripItemRepo.findByUid(uid);
        if(tripItemModel.getStatus() != null) {
            tripItem.setStatus(tripItemModel.getStatus());
            if(tripItemModel.getStatus().equals(Constants.OrderStatus.EXECUTING.getStatus())
            && tripItemModel.getAction().equals("PICKUP_CONTAINER")) {
                Order order = orderRepo.findByUid(tripItemModel.getOrderUid());
                order.setStatus(Constants.OrderStatus.EXECUTING.getStatus());
                orderRepo.save(order);

                Container container = containerRepo.findByUid(order.getContainer().getUid());
                container.setStatus(Constants.ContainerStatus.EXECUTING.getStatus());
                container.setUpdatedAt(System.currentTimeMillis());
                containerRepo.save(container);
            }
            if(tripItemModel.getStatus().equals(Constants.OrderStatus.DONE.getStatus())
                    && tripItemModel.getAction().contains("DELIVERY_CONTAINER")) {
                Order order = orderRepo.findByUid(tripItemModel.getOrderUid());
                order.setStatus(Constants.OrderStatus.DONE.getStatus());
                orderRepo.save(order);

                Facility toFacility = facilityRepo.findById(tripItemModel.getFacilityId()).get();

                Container container = containerRepo.findByUid(order.getContainer().getUid());
                container.setStatus(Constants.ContainerStatus.AVAILABLE.getStatus());
                container.setOwner(order.getCustomerId());
                container.setFacility(toFacility);
                container.setUpdatedAt(System.currentTimeMillis());
                containerRepo.save(container);
            }

            if(tripItemModel.getStatus().equals(Constants.OrderStatus.DONE.getStatus())
                    && tripItemModel.getAction().equals("DEPART")) {
                Trip trip = tripRepo.findByUid(tripItemModel.getTripId());
                Truck truck = truckRepo.findByUid(trip.getTruck().getUid());
                truck.setStatus(Constants.TruckStatus.EXECUTING.getStatus());
                truck.setUpdatedAt(System.currentTimeMillis());
                truckRepo.save(truck);
            }

            if(tripItemModel.getStatus().equals(Constants.OrderStatus.DONE.getStatus())
                    && tripItemModel.getAction().equals("PICKUP_TRAILER")) {
                Trailer trailer = trailerRepo.findById(tripItemModel.getTrailerId()).get();
                trailer.setStatus(Constants.TrailerStatus.EXECUTING.getStatus());
                trailer.setUpdatedAt(System.currentTimeMillis());
                trailerRepo.save(trailer);
            }

            if(tripItemModel.getStatus().equals(Constants.OrderStatus.DONE.getStatus())
                    && tripItemModel.getAction().equals("DROP_TRAILER")) {
                Trailer trailer = trailerRepo.findById(tripItemModel.getTrailerId()).get();
                trailer.setStatus(Constants.TrailerStatus.AVAILABLE.getStatus());

                Facility toFacility = facilityRepo.findById(tripItemModel.getFacilityId()).get();
                trailer.setFacility(toFacility);
                trailer.setUpdatedAt(System.currentTimeMillis());
                trailerRepo.save(trailer);
            }

            if(tripItemModel.getStatus().equals(Constants.OrderStatus.DONE.getStatus())
                    && tripItemModel.getAction().equals("STOP")) {
                Trip trip = tripRepo.findByUid(tripItemModel.getTripId());
                trip.setStatus(Constants.TripStatus.DONE.getStatus());
                trip.setUpdatedAt(System.currentTimeMillis());
                tripRepo.save(trip);

                Facility toFacility = facilityRepo.findById(tripItemModel.getFacilityId()).get();

                Truck truck = truckRepo.findByUid(trip.getTruck().getUid());
                truck.setStatus(Constants.TruckStatus.AVAILABLE.getStatus());
                truck.setFacility(toFacility);
                truck.setUpdatedAt(System.currentTimeMillis());
                truckRepo.save(truck);

                List<Trip> tripList = tripRepo.getTripByShipmentId(trip.getShipment().getUid());
                List<Trip> tripListDone = tripList.stream().filter((item) -> item.getStatus().equals(Constants.ShipmentStatus.DONE.getStatus())).collect(Collectors.toList());
                if(tripList.size() == tripListDone.size()) {
                    Shipment shipment = shipmentRepo.findByUid(trip.getShipment().getUid());
                    shipment.setStatus(Constants.ShipmentStatus.DONE.getStatus());
                    shipment.setUpdatedAt(System.currentTimeMillis());
                    shipmentRepo.save(shipment);
                }
            }
        }
        tripItem = tripItemRepo.save(tripItem);
        return convertToModel(tripItem);
    }

    @Override
    public TripItemModel deleteTripItem(String uid) {
        TripItem tripItemDelete = tripItemRepo.deleteByUid(uid);
        return convertToModel(tripItemDelete);
    }

    public TripItemModel convertToModel (TripItem tripItem) {
        TripItemModel tripItemModel = modelMapper.map(tripItem, TripItemModel.class);
        tripItemModel.setFacilityName(tripItem.getFacility().getFacilityName());
        tripItemModel.setFacilityId(tripItem.getFacility().getId());
        tripItemModel.setFacilityCode(tripItem.getFacility().getFacilityCode());
        tripItemModel.setLongitude(tripItem.getFacility().getLongitude());
        tripItemModel.setLatitude(tripItem.getFacility().getLatitude());
        tripItemModel.setTruckId(tripItem.getTrip().getTruck().getId());
        tripItemModel.setTripId(tripItem.getTrip().getUid());
        if(tripItem.getOrder() != null) {
            tripItemModel.setOrderUid(tripItem.getOrder().getUid());
            if(tripItemModel.getAction().equals("PICKUP_CONTAINER") && tripItem.getOrder().getLatePickupTime() > 0) {
                tripItemModel.setLateTime(tripItem.getOrder().getLatePickupTime());
            }
            if (tripItemModel.getAction().contains("DELIVERY_CONTAINER") && tripItem.getOrder().getLateDeliveryTime() > 0){
                tripItemModel.setLateTime(tripItem.getOrder().getLateDeliveryTime());
            }
        }
        if(tripItem.getContainer() != null) {
            tripItemModel.setContainerCode(tripItem.getContainer().getContainerCode());
            tripItemModel.setContainerId(tripItem.getContainer().getId());
            tripItemModel.setContainer(containerService.convertToModel(tripItem.getContainer()));
        }
        if (tripItem.getTrailer() != null) {
            tripItemModel.setTrailerCode(tripItem.getTrailer().getTrailerCode());
            tripItemModel.setTrailerId(tripItem.getTrailer().getId());
        }
        return tripItemModel;
    }
}
