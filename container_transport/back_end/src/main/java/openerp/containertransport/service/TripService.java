package openerp.containertransport.service;

import openerp.containertransport.dto.TripModel;
import openerp.containertransport.entity.Trip;

public interface TripService {
    TripModel createTrip (TripModel tripModel, long shipmentId, String createBy);

    TripModel filterTrip ();
}
