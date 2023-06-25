package openerp.containertransport.service.impl;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.constants.Constants;
import openerp.containertransport.dto.TripItemModel;
import openerp.containertransport.entity.*;
import openerp.containertransport.repo.*;
import openerp.containertransport.service.TripItemService;
import openerp.containertransport.utils.RandomUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripItemServiceImpl implements TripItemService {
    private final TripItemRepo tripItemRepo;
    private final ModelMapper modelMapper;
    private final FacilityRepo facilityRepo;
    private final ContainerRepo containerRepo;
    private final TrailerRepo trailerRepo;
    private final TripRepo tripRepo;
    @Override
    public TripItemModel createTripItem(TripItemModel tripItemModel, String tripUid) {
        TripItem tripItem = new TripItem();
        Facility facility = facilityRepo.findById(tripItemModel.getFacilityId()).get();
        if(tripItemModel.getContainerId() != null) {
            Container container = containerRepo.findById(tripItemModel.getContainerId()).get();
            container.setStatus(Constants.ContainerStatus.SCHEDULED.getStatus());

            tripItem.setContainer(container);
            containerRepo.save(container);
        }
        if(tripItemModel.getTrailerId() != null) {
            Trailer trailer = trailerRepo.findById(tripItemModel.getTrailerId()).get();
            trailer.setStatus(Constants.TrailerStatus.SCHEDULED.getStatus());

            tripItem.setTrailer(trailer);
            trailerRepo.save(trailer);
        }
        if(tripItemModel.getOrderCode() != null) {
            tripItem.setOrderCode(tripItemModel.getOrderCode());
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
        if(tripItem.getContainer() != null) {
            tripItemModel.setContainerCode(tripItem.getContainer().getContainerCode());
        }
        if (tripItem.getTrailer() != null) {
            tripItemModel.setTrailerCode(tripItem.getTrailer().getTrailerCode());
        }
        return tripItemModel;
    }
}
