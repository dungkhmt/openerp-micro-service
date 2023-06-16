package openerp.containertransport.service;

import openerp.containertransport.dto.TripFilterRequestDTO;
import openerp.containertransport.dto.TripModel;
import openerp.containertransport.entity.Trip;

import java.util.List;

public interface TripService {
    TripModel createTrip (TripModel tripModel, long shipmentId, String createBy);

    List<TripModel> filterTrip (TripFilterRequestDTO requestDTO);

    TripModel getById(long id);

    List<TripModel> getTripsByDriver(TripFilterRequestDTO requestDTO);

    TripModel updateTrip(Long id, TripModel tripModel);
}
