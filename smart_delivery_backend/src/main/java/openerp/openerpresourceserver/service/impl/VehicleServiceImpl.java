package openerp.openerpresourceserver.service.impl;

import jakarta.ws.rs.NotFoundException;
import openerp.openerpresourceserver.dto.VehicleDto;
import openerp.openerpresourceserver.entity.Vehicle;
import openerp.openerpresourceserver.mapper.VehicleMapper;
import openerp.openerpresourceserver.repository.VehicleRepository;
import openerp.openerpresourceserver.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleMapper vehicleMapper = VehicleMapper.INSTANCE;
    @Autowired
    private VehicleRepository vehicleRepo;


    @Override
    public List<VehicleDto> getAllVehicleByHubId(UUID hubId){
        List<VehicleDto> vehicleDtos = vehicleRepo.getVehicleByHubId(hubId);
        if(vehicleDtos.isEmpty()) throw new NotFoundException("not found any vehicle for hub");
        return vehicleDtos;
    };


    @Override
    public List<VehicleDto> getAll(){
        List<Vehicle> vehicles = vehicleRepo.findAll();
        if(vehicles.isEmpty()) throw new NotFoundException("not found any vehicle");
        List<VehicleDto> vehicleDtos = new ArrayList<>();
        for(Vehicle vehicle : vehicles){
            System.out.println(vehicle);
            System.out.println(vehicleMapper.vehicleToVehicleDto(vehicle));
            vehicleDtos.add(vehicleMapper.vehicleToVehicleDto(vehicle));

        }
        return vehicleDtos;
    }
    @Override
    public VehicleDto getVehicleById(UUID vehicleId) {
        Vehicle vehicle = vehicleRepo.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + vehicleId));
        return vehicleMapper.vehicleToVehicleDto(vehicle);
    }
}
