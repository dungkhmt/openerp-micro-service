package openerp.containertransport.service;

import openerp.containertransport.dto.TripItemModel;
import openerp.containertransport.entity.TripItem;

import java.util.List;

public interface TripItemService {
    TripItemModel createTripItem(TripItemModel tripItemModel, String tripUid);

    List<TripItemModel> getTripItemByTripId(String uid);

    TripItemModel updateTripItem(String uid, TripItemModel tripItemModel);
}
