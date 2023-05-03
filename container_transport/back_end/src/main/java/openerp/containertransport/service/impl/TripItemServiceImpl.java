package openerp.containertransport.service.impl;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.dto.TripItemModel;
import openerp.containertransport.entity.Facility;
import openerp.containertransport.entity.TripItem;
import openerp.containertransport.repo.FacilityRepo;
import openerp.containertransport.repo.TripItemRepo;
import openerp.containertransport.service.TripItemService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripItemServiceImpl implements TripItemService {
    private final TripItemRepo tripItemRepo;
    private final ModelMapper modelMapper;
    private final FacilityRepo facilityRepo;
    @Override
    public TripItemModel createTripItem(TripItemModel tripItemModel, long tripId) {
        TripItem tripItem = new TripItem();
        Facility facility = facilityRepo.findById(tripItemModel.getFacilityId()).get();
        tripItem.setTripId(tripId);
        tripItem.setSeq(tripItem.getSeq());
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
    public TripItemModel filterTripItem() {
        return null;
    }

    public TripItemModel convertToModel (TripItem tripItem) {
        TripItemModel tripItemModel = modelMapper.map(tripItem, TripItemModel.class);
        tripItemModel.setFacilityName(tripItem.getFacility().getFacilityName());
        tripItemModel.setFacilityId(tripItem.getFacility().getId());
        return tripItemModel;
    }
}
