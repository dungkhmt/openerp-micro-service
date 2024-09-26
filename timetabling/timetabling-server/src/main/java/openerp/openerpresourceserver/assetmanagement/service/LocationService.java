package openerp.openerpresourceserver.assetmanagement.service;

import openerp.openerpresourceserver.assetmanagement.entity.Location;

import java.util.List;
import java.util.Optional;

public interface LocationService {
    List<Location> getAllLocations();

    Optional<Location> getLocationById(Integer Id);

    Location addNewLocation(Location location);

    Location editLocation(Integer Id, Location location);

    void deleteLocation(Integer Id);
}
