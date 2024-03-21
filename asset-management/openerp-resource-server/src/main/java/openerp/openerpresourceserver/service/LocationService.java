package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.Location;

import java.util.List;

public interface LocationService {
    List<Location> getAllLocations();

    Location addNewLocation(Location location);
}
