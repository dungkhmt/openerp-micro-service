package openerp.openerpresourceserver.service;

import java.util.List;

import openerp.openerpresourceserver.entity.Amenity;

public interface AmenityService {
    Amenity getAmenityById(Long amenityId);

    List<Amenity> getAllAmenities();

    List<Amenity> searchAmenities(String keyword);

    Amenity createAmenity(Amenity amenity);

    Amenity updateAmenity(Long amenityId, Amenity amenity);

    void deleteAmenity(Long amenityId);
    
}
