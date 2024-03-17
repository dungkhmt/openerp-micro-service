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
}
