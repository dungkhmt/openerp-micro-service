package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.Location;

import java.util.List;

public interface LocationService {
    List<Location> getAllLocations();

    Location addNewLocation(Location location);

    Location editLocation(Integer Id, Location location);

    void deleteLocation(Integer Id);
}
