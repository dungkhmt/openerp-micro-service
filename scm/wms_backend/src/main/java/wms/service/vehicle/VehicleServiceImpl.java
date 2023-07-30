package wms.service.vehicle;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.hibernate.internal.util.StringHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wms.common.enums.ErrorCode;
import wms.dto.ReturnPaginationDTO;
import wms.dto.vehicle.DroneDTO;
import wms.dto.vehicle.TruckDTO;
import wms.entity.*;
import wms.exception.CustomException;
import wms.repo.DroneRepo;
import wms.repo.TruckRepo;
import wms.repo.UserRepo;
import wms.service.BaseService;
import wms.utils.GeneralUtils;

@Service
public class VehicleServiceImpl extends BaseService implements IVehicleService {
    private final UserRepo userRepo;
    private final TruckRepo truckRepo;
    private final DroneRepo droneRepo;

    public VehicleServiceImpl(UserRepo userRepo,
                              TruckRepo truckRepo,
                              DroneRepo droneRepo) {
        this.userRepo = userRepo;
        this.truckRepo = truckRepo;
        this.droneRepo = droneRepo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TruckEntity createTruck(TruckDTO truckDTO) throws CustomException {
        UserRegister userLogin = userRepo.getUserByUserLoginId(truckDTO.getUserManaged());
        TruckEntity truck = truckRepo.getTruckFromUser(truckDTO.getUserManaged());
        if (truck != null) {
            throw caughtException(ErrorCode.ALREADY_EXIST.getCode(), "User has been in charged of another truck");
        }
        TruckEntity newTruck = TruckEntity.builder()
                .code("TRUCK" + GeneralUtils.generateCodeFromSysTime())
                .capacity(truckDTO.getCapacity())
                .size(truckDTO.getSize())
                .name(truckDTO.getName())
                .speed(truckDTO.getSpeed())
                .transportCostPerUnit(truckDTO.getTransportCostPerUnit())
                .waitingCost(truckDTO.getWaitingCost())
                .userLogin(userLogin)
                .build();
        return truckRepo.save(newTruck);
    }

    @Override
    public ReturnPaginationDTO<TruckEntity> getAllTrucks(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<TruckEntity> truckList = truckRepo.search(pageable);
        return getPaginationResult(truckList.getContent(), page, truckList.getTotalPages(), truckList.getTotalElements());
    }

    @Override
    public TruckEntity getTruckById(long id) {
        return truckRepo.getTruckById(id);
    }

    @Override
    public TruckEntity getTruckByCode(String code) {
        return truckRepo.getTruckByCode(code);
    }

    @Override
    public TruckEntity updateTruck(TruckDTO truckDTO, long id) throws CustomException {
        UserRegister manager = userRepo.getUserByUserLoginId(truckDTO.getUserManaged());
        TruckEntity truck = truckRepo.getTruckById(id);
        if (truck == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Unknown truck");
        }
        truck.setCapacity(truckDTO.getCapacity());
        truck.setSize(truckDTO.getSize());
        truck.setName(truckDTO.getName());
        truck.setSpeed(truckDTO.getSpeed());
        truck.setTransportCostPerUnit(truckDTO.getTransportCostPerUnit());
        truck.setWaitingCost(truckDTO.getWaitingCost());
        truck.setUserLogin(manager != null ? manager : truck.getUserLogin());
        return truckRepo.save(truck);
    }
    @Override
    public void deleteTruckById(long id) {
        truckRepo.deleteById(id);
    }

    @Override
    public void deleteTruckByCode(String code) throws CustomException {
        TruckEntity truck = getTruckByCode(code);
        if (truck == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Truck does not exists, can't delete");
        }
        truck.setDeleted(1);
        truckRepo.save(truck);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DroneEntity createDrone(DroneDTO droneDTO) throws CustomException {
        UserRegister userLogin = userRepo.getUserByUserLoginId(droneDTO.getUserManaged());
        DroneEntity truck = droneRepo.getDroneFromUser(droneDTO.getUserManaged());
        if (truck != null) {
            throw caughtException(ErrorCode.ALREADY_EXIST.getCode(), "User has been in charged of another drone");
        }
        DroneEntity newDrone = DroneEntity.builder()
                .code("DRONE" + GeneralUtils.generateCodeFromSysTime())
                .capacity(droneDTO.getCapacity())
                .name(droneDTO.getName())
                .speed(droneDTO.getSpeed())
                .durationTime(droneDTO.getDuration())
                .transportCostPerUnit(droneDTO.getTransportCostPerUnit())
                .waitingCost(droneDTO.getWaitingCost())
                .userLogin(userLogin)
                .build();
        return droneRepo.save(newDrone);
    }

    @Override
    public ReturnPaginationDTO<DroneEntity> getAllDrones(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<DroneEntity> droneList = droneRepo.search(pageable);
        return getPaginationResult(droneList.getContent(), page, droneList.getTotalPages(), droneList.getTotalElements());
    }

    @Override
    public DroneEntity getDroneById(long id) {
        return droneRepo.getDroneById(id);
    }

    @Override
    public DroneEntity getDroneByCode(String code) {
        return droneRepo.getDroneByCode(code);
    }

    @Override
    public DroneEntity updateDrone(DroneDTO droneDTO, long id) throws CustomException {
        UserRegister manager = userRepo.getUserByUserLoginId(droneDTO.getUserManaged());
        DroneEntity drone = droneRepo.getDroneById(id);
        if (drone == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Unknown drone");
        }
        drone.setCapacity(droneDTO.getCapacity());
        drone.setName(droneDTO.getName());
        drone.setSpeed(droneDTO.getSpeed());
        drone.setTransportCostPerUnit(droneDTO.getTransportCostPerUnit());
        drone.setWaitingCost(droneDTO.getWaitingCost());
        drone.setUserLogin(manager != null ? manager : drone.getUserLogin());
        drone.setDurationTime(droneDTO.getDuration());
        return droneRepo.save(drone);
    }

    @Override
    public void deleteDroneById(long id) {
        droneRepo.deleteById(id);
    }

    @Override
    public void deleteDroneByCode(String code) throws CustomException {
        DroneEntity drone = getDroneByCode(code);
        if (drone == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Drone does not exists, can't delete");
        }
        drone.setDeleted(1);
        droneRepo.save(drone);
    }
}
