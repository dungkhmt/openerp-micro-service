package openerp.openerpresourceserver.service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Location;
import openerp.openerpresourceserver.repo.LocationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class LocationServiceImpl implements LocationService{
    private LocationRepo locationRepo;

    @Override
    public List<Location> getAllLocations() {
        List<Location> locations = locationRepo.findAll();
        return locations;
    }

    @Override
    public Location addNewLocation(Location location) {
        Location loc = new Location();
        loc.setName(location.getName());
        loc.setAddress(location.getAddress());
        loc.setDescription(location.getDescription());
        loc.setImage(location.getImage());
        return locationRepo.save(loc);
    }
}
