package com.example.api.services.bus;

import com.example.api.controllers.admin.dto.BusManipulateParam;
import com.example.api.services.bus.dto.GetBusDetailOutput;
import com.example.api.services.bus.dto.GetListBusOutput;
import com.example.api.services.bus.dto.GetListManipulateBusOutPut;
import com.example.api.services.bus.dto.ListBusFilterParam;
import com.example.api.services.bus.dto.AddBusInput;
import com.example.api.services.bus.dto.UpdateBusEmployeeInput;
import com.example.api.services.bus.dto.UpdateBusInput;
import com.example.api.services.common_dto.BusOutput;
import com.example.api.services.common_dto.EmployeeOutput;
import com.example.shared.db.dto.GetListBusDTO;
import com.example.shared.db.entities.Account;
import com.example.shared.db.entities.Bus;
import com.example.shared.db.entities.Employee;
import com.example.shared.db.entities.Ride;
import com.example.shared.db.repo.BusRepository;
import com.example.shared.db.repo.EmployeeRepository;
import com.example.shared.db.repo.PickupPointRepository;
import com.example.shared.db.repo.RidePickupPointRepository;
import com.example.shared.db.repo.RideRepository;
import com.example.shared.enumeration.BusStatus;
import com.example.shared.enumeration.EmployeeRole;
import com.example.shared.enumeration.RideStatus;
import com.example.shared.exception.MyException;
import com.example.shared.utils.DateConvertUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BusServiceImpl implements BusService {
    private final BusRepository busRepository;
    private final EmployeeRepository employeeRepository;
    private final RideRepository rideRepository;
    private final PickupPointRepository pickupPointRepository;
    private final RidePickupPointRepository ridePickupPointRepository;

    @Override
    public Page<GetListBusOutput> getListBus(ListBusFilterParam filterParam, Pageable pageable) {
        List<BusStatus> statuses = filterParam.getStatuses();
        if (statuses != null && statuses.isEmpty()) {
            statuses = null;
        }
        try {
            Page<GetListBusDTO> getListBusDTOS = busRepository.getListBus(
                filterParam.getNumberPlate(),
                filterParam.getSeatNumber(),
                statuses,
                filterParam.getDriverName(),
                filterParam.getDriverId(),
                filterParam.getDriverMateName(),
                filterParam.getDriverMateId(),
                pageable
            );
            return getListBusDTOS.map(GetListBusOutput::fromDto);
        } catch (Exception e) {
            throw new MyException(null,
                "GET_LIST_BUS_ERROR",
                "Invalid input",
                HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public GetBusDetailOutput getBus(Long busId) {
        Bus bus = busRepository.findById(busId).orElseThrow(
            () -> new MyException(null,
                "BUS_NOT_FOUND",
                "Bus with id " + busId + " not found",
                HttpStatus.NOT_FOUND)
        );

        Employee driver = (bus.getDriverId() != null) ?
            employeeRepository.findById(bus.getDriverId()).orElse(null)
            : null;
        Employee driverMate = (bus.getDriverMateId() != null) ?
            employeeRepository.findById(bus.getDriverMateId()).orElse(null)
            : null;

        return GetBusDetailOutput.builder()
            .bus(BusOutput.fromEntity(bus))
            .driver(driver == null ? null : EmployeeOutput.fromEntity(driver))
            .driverMate(driverMate == null ? null : EmployeeOutput.fromEntity(driverMate))
            .build();
    }

    @Override
    @Transactional
    public void addBus(AddBusInput input) {
        Bus bus = busRepository.findByNumberPlate(input.getNumberPlate());
        if (bus != null) {
            throw new MyException(null,
                "BUS_ALREADY_EXISTS",
                "Bus with number plate " + input.getNumberPlate() + " already exists",
                HttpStatus.BAD_REQUEST);
        }

        Bus newBus = Bus.builder()
            .numberPlate(input.getNumberPlate())
            .seatNumber(input.getSeatNumber())
            .status(input.getStatus())
            .driverId(input.getDriverId())
            .driverMateId(input.getDriverMateId())
            .build();
        busRepository.save(newBus);

        if (input.getDriverId() != null) {
            Employee driver = employeeRepository.findById(input.getDriverId())
                .orElseThrow(() -> new MyException(null,
                    "DRIVER_NOT_FOUND",
                    "Driver with id " + input.getDriverId() + " not found",
                    HttpStatus.NOT_FOUND));
            driver.setBusId(newBus.getId());
            employeeRepository.save(driver);
        }
        if (input.getDriverMateId() != null) {
            Employee driverMate = employeeRepository.findById(input.getDriverMateId())
                .orElseThrow(() -> new MyException(null,
                    "DRIVER_MATE_NOT_FOUND",
                    "Driver mate with id " + input.getDriverMateId() + " not found",
                    HttpStatus.NOT_FOUND));
            driverMate.setBusId(newBus.getId());
            employeeRepository.save(driverMate);
        }
    }

    @Override
    @Transactional
    public void updateBus(UpdateBusInput input) {
        Bus bus = busRepository.findById(input.getId())
            .orElseThrow(() -> new MyException(null,
                "BUS_NOT_FOUND",
                "Bus with id " + input.getId() + " not found",
                HttpStatus.NOT_FOUND));

        // validate driver and driver mate
        if (input.getDriverId() != null) {
            Employee driver = employeeRepository.findById(input.getDriverId())
                .orElseThrow(() -> new MyException(null,
                    "DRIVER_NOT_FOUND",
                    "Driver with id " + input.getDriverId() + " not found",
                    HttpStatus.NOT_FOUND));
            if (busRepository.existsByDriverId(input.getDriverId())
                && !bus.getDriverId().equals(input.getDriverId())) {
                throw new MyException(null,
                    "DRIVER_ALREADY_ASSIGNED",
                    "Driver with id " + input.getDriverId() + " already assigned to another bus",
                    HttpStatus.BAD_REQUEST);
            }

            // update old driver
            if (bus.getDriverId() != null && !bus.getDriverId().equals(input.getDriverId())) {
                Employee oldDriver = employeeRepository.findById(bus.getDriverId())
                    .orElseThrow(() -> new MyException(null,
                        "DRIVER_NOT_FOUND",
                        "Driver with id " + bus.getDriverId() + " not found",
                        HttpStatus.NOT_FOUND));
                oldDriver.setBusId(null);
                employeeRepository.save(oldDriver);
            }

            // update new driver
            driver.setBusId(bus.getId());
            employeeRepository.save(driver);
        } else if (input.getDriverId() == null && bus.getDriverId() != null) {
            Employee oldDriver = employeeRepository.findById(bus.getDriverId())
                .orElseThrow(() -> new MyException(null,
                    "DRIVER_NOT_FOUND",
                    "Driver with id " + bus.getDriverId() + " not found",
                    HttpStatus.NOT_FOUND));
            oldDriver.setBusId(null);
            employeeRepository.save(oldDriver);
        }

        if (input.getDriverMateId() != null) {
            Employee driverMate = employeeRepository.findById(input.getDriverMateId())
                .orElseThrow(() -> new MyException(null,
                    "DRIVER_MATE_NOT_FOUND",
                    "Driver mate with id " + input.getDriverMateId() + " not found",
                    HttpStatus.NOT_FOUND));
            if (busRepository.existsByDriverMateId(input.getDriverMateId())
                && !bus.getDriverMateId().equals(input.getDriverMateId())) {
                throw new MyException(null,
                    "DRIVER_MATE_ALREADY_ASSIGNED",
                    "Driver mate with id " + input.getDriverMateId() + " already assigned to another bus",
                    HttpStatus.BAD_REQUEST);
            }

            // update old driver mate
            if (bus.getDriverMateId() != null && !bus.getDriverMateId().equals(input.getDriverMateId())) {
                Employee oldDriverMate = employeeRepository.findById(bus.getDriverMateId())
                    .orElseThrow(() -> new MyException(null,
                        "DRIVER_MATE_NOT_FOUND",
                        "Driver mate with id " + bus.getDriverMateId() + " not found",
                        HttpStatus.NOT_FOUND));
                oldDriverMate.setBusId(null);
                employeeRepository.save(oldDriverMate);
            }
            // update new driver mate
            driverMate.setBusId(bus.getId());
            employeeRepository.save(driverMate);
        } else if (input.getDriverMateId() == null && bus.getDriverMateId() != null) {
            Employee oldDriverMate = employeeRepository.findById(bus.getDriverMateId())
                .orElseThrow(() -> new MyException(null,
                    "DRIVER_MATE_NOT_FOUND",
                    "Driver mate with id " + bus.getDriverMateId() + " not found",
                    HttpStatus.NOT_FOUND));
            oldDriverMate.setBusId(null);
            employeeRepository.save(oldDriverMate);
        }


//        bus.setNumberPlate(input.getNumberPlate()); // not allow to update number plate
        bus.setSeatNumber(input.getSeatNumber());
        bus.setStatus(input.getStatus());
        bus.setDriverId(input.getDriverId());
        bus.setDriverMateId(input.getDriverMateId());
        busRepository.save(bus);
    }

    @Override
    @Transactional
    public void deleteBus(Long id) {
        Bus bus = busRepository.findById(id)
            .orElseThrow(() -> new MyException(null,
                "BUS_NOT_FOUND",
                "Bus with id " + id + " not found",
                HttpStatus.NOT_FOUND));
        List<Employee> employees = employeeRepository.findByBusId(id).orElse(null);

        if (employees != null && !employees.isEmpty()) {
            for (Employee employee : employees) {
                employee.setBusId(null);
            }
            employeeRepository.saveAll(employees);
        }

        busRepository.delete(bus);
    }

    @Override
    public List<Bus> getAvailableBuses(EmployeeRole role, String query) {
        return busRepository.getAvailableBuses((role != null) ? role.toString() : null, query);
    }

    @Override
    public Page<GetListManipulateBusOutPut> getListManipulateBusPage(BusManipulateParam param,
                                                                     Pageable pageable) {
        Page<Bus> buses = busRepository.findAllByNumberPlateAndStatus(
            param.getNumberPlate(),
            param.getStatus(),
            pageable
        );

        return buses.map(bus -> {
            List<Ride> rides = rideRepository.findByManipulateRideNotInStatus(
                bus.getId(),
                RideStatus.FINISHED,
                param.getIsToSchool(),
                DateConvertUtil.convertStringToInstant(param.getDate())
            );

            if (rides.size() > 1) {
//                throw new MyException(null,
//                    "MULTIPLE_PENDING_RIDES",
//                    "Multiple pending rides found for bus with id " + bus.getId()
//                        + " on date " + param.getDate()
//                        + " and isToSchool " + param.getIsToSchool(),
//                    HttpStatus.INTERNAL_SERVER_ERROR);

                // sort to get ride with bigger ID (cause ID is auto increment)
                rides.sort((r1, r2) -> r2.getId().compareTo(r1.getId()));
            }

            Ride ride = rides.isEmpty() ? null : rides.get(0);
            return GetListManipulateBusOutPut.builder()
                .bus(bus)
                .ride(ride)
                .pickupPoints(ride == null ? null : pickupPointRepository.findByRideId(ride.getId()))
                .build();
        });
    }

    @Override
    @Transactional
    public void updateBusEmployee(UpdateBusEmployeeInput input, Account account) {

        Employee employee = employeeRepository.findByAccountId(account.getId())
            .orElseThrow(() -> new MyException(null,
                "EMPLOYEE_NOT_FOUND",
                "Employee not found",
                HttpStatus.NOT_FOUND));

        // find by number plate
        Bus bus = busRepository.findByNumberPlate(input.getNumberPlate());
        if (bus == null) {
            throw new MyException(null,
                "BUS_NOT_FOUND",
                "Bus with number plate " + input.getNumberPlate() + " not found",
                HttpStatus.NOT_FOUND);
        }

        // validate account have this bus
        if (!employee.getBusId().equals(bus.getId())) {
            throw new MyException(null,
                "BUS_NOT_ASSIGNED",
                "Bus with number plate " + input.getNumberPlate() + " not assigned to this account",
                HttpStatus.BAD_REQUEST);
        }

        bus.setStatus(input.getStatus());
        busRepository.save(bus);
    }

}
