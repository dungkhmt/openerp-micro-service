package openerp.containertransport.service;

import openerp.containertransport.dto.TripItemModel;
import openerp.containertransport.entity.TripItem;

import java.util.List;

public interface TripItemService {
    TripItemModel createTripItem(TripItemModel tripItemModel, long tripId);

    List<TripItemModel> getTripItemByTripId(long id);
}
