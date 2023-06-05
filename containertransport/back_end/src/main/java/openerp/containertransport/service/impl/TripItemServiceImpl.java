package openerp.containertransport.service.impl;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.dto.TripItemModel;
import openerp.containertransport.entity.Container;
import openerp.containertransport.entity.Facility;
import openerp.containertransport.entity.Trailer;
import openerp.containertransport.entity.TripItem;
import openerp.containertransport.repo.ContainerRepo;
import openerp.containertransport.repo.FacilityRepo;
import openerp.containertransport.repo.TrailerRepo;
import openerp.containertransport.repo.TripItemRepo;
import openerp.containertransport.service.TripItemService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripItemServiceImpl implements TripItemService {
    private final TripItemRepo tripItemRepo;
    private final ModelMapper modelMapper;
    private final FacilityRepo facilityRepo;
    private final ContainerRepo containerRepo;
    private final TrailerRepo trailerRepo;
    @Override
    public TripItemModel createTripItem(TripItemModel tripItemModel, long tripId) {
        TripItem tripItem = new TripItem();
        Facility facility = facilityRepo.findById(tripItemModel.getFacilityId()).get();
        if(tripItemModel.getContainerId() != null) {
            Container container = containerRepo.findById(tripItemModel.getContainerId()).get();
            tripItem.setContainer(container);
        }
        if(tripItemModel.getTrailerId() != null) {
            Trailer trailer = trailerRepo.findById(tripItemModel.getTrailerId()).get();
            tripItem.setTrailer(trailer);
        }
        if(tripItemModel.getOrderCode() != null) {
            tripItem.setOrderCode(tripItemModel.getOrderCode());
        }
        tripItem.setTripId(tripId);
        tripItem.setSeq((int) tripItemModel.getSeq());
        tripItem.setAction(tripItemModel.getAction());
        tripItem.setFacility(facility);
        tripItem.setStatus("WAITING");
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
    public List<TripItemModel> getTripItemByTripId(long id) {
        List<TripItem> tripItems = tripItemRepo.findByTripId(id);
        List<TripItemModel> tripItemModels = new ArrayList<>();
        tripItems.forEach((tripItem -> tripItemModels.add(convertToModel(tripItem))));
        return tripItemModels;
    }

    public TripItemModel convertToModel (TripItem tripItem) {
        TripItemModel tripItemModel = modelMapper.map(tripItem, TripItemModel.class);
        tripItemModel.setFacilityName(tripItem.getFacility().getFacilityName());
        tripItemModel.setFacilityId(tripItem.getFacility().getId());
        tripItemModel.setFacilityCode(tripItem.getFacility().getFacilityCode());
        tripItemModel.setLongitude(tripItem.getFacility().getLongitude());
        tripItemModel.setLatitude(tripItem.getFacility().getLatitude());
        if(tripItem.getContainer() != null) {
            tripItemModel.setContainerCode(tripItem.getContainer().getContainerCode());
        }
        if (tripItem.getTrailer() != null) {
            tripItemModel.setTrailerCode(tripItem.getTrailer().getTrailerCode());
        }
        return tripItemModel;
    }
}
