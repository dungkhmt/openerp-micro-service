package openerp.openerpresourceserver.assetmanagement.service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.assetmanagement.entity.Location;
import openerp.openerpresourceserver.assetmanagement.repo.LocationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class LocationServiceImpl implements LocationService{
    private LocationRepo locationRepo;

    @Override
    public List<Location> getAllLocations() {
        return locationRepo.getAllByLastUpdate();
    }

    @Override
    public Optional<Location> getLocationById(Integer Id) {
        Optional<Location> location = locationRepo.findById(Id);
        return location;
    }

    @Override
    public Location addNewLocation(Location location) {
        Location loc = new Location();
        loc.setName(location.getName());
        loc.setAddress(location.getAddress());
        loc.setDescription(location.getDescription());
        loc.setImage(location.getImage());
        loc.setNum_assets(0);

        loc.setSince(new Date());
        loc.setLast_updated(new Date());
        return locationRepo.save(loc);
    }

    @Override
    public Location editLocation(Integer Id, Location location) {
        Location foundLocation = locationRepo.findById(Id).get();
        foundLocation.setName(location.getName());
        foundLocation.setAddress(location.getAddress());
        foundLocation.setDescription(location.getDescription());
        foundLocation.setImage(location.getImage());
        return locationRepo.save(foundLocation);
    }

    @Override
    public void deleteLocation(Integer Id) {
        Optional<Location> foundLocation = locationRepo.findById(Id);
        if(foundLocation.isPresent()){
            locationRepo.deleteById(Id);
        }
    }
}
