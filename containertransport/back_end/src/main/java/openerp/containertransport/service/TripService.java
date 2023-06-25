package openerp.containertransport.service;

import openerp.containertransport.dto.TripDeleteDTO;
import openerp.containertransport.dto.TripFilterRequestDTO;
import openerp.containertransport.dto.TripModel;
import openerp.containertransport.entity.Trip;

import java.util.List;

public interface TripService {
    TripModel createTrip (TripModel tripModel, String shipmentId, String createBy);

    List<TripModel> filterTrip (TripFilterRequestDTO requestDTO);

    TripModel getByUid(String uid);

    List<TripModel> getTripsByDriver(TripFilterRequestDTO requestDTO);

    TripModel updateTrip(String uid, TripModel tripModel);

    List<TripModel> deleteTrips(TripDeleteDTO tripDeleteDTO);
}
