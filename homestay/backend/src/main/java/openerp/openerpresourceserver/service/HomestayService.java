package openerp.openerpresourceserver.service;

import java.util.List;

import openerp.openerpresourceserver.entity.Homestay;

public interface HomestayService {
    Homestay getHomestayById(Long homestayId);

    List<Homestay> getAllHomestays();

    List<Homestay> getHomestaysByHost(Long hostId);

    List<Homestay> getHomestaysByCity(String city);

    List<Homestay> searchHomestays(String keyword);

    Homestay createHomestay(Homestay homestay);

    Homestay updateHomestay(Long homestayId, Homestay homestay);

    void softDeleteHomestay(Long homestayId);

    void addAmenityToHomestay(Long homestayId, Long amenityId);

    void removeAmenityFromHomestay(Long homestayId, Long amenityId);
    
}
