package openerp.containertransport.service;

import openerp.containertransport.dto.TripItemModel;
import openerp.containertransport.entity.TripItem;

public interface TripItemService {
    TripItemModel createTripItem(TripItemModel tripItemModel, long tripId);

    TripItemModel filterTripItem();
}
