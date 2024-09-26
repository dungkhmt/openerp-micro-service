package com.example.api.services.pickup_point;

import com.example.api.controllers.admin.dto.PickupPointFilterParam;
import com.example.api.services.common_dto.BusOutput;
import com.example.api.services.common_dto.EmployeeOutput;
import com.example.api.services.common_dto.PickupPointOutput;
import com.example.api.services.common_dto.RideOutput;
import com.example.api.services.common_dto.RidePickupPointOutput;
import com.example.api.services.common_dto.StudentOutput;
import com.example.api.services.common_dto.StudentPickupPointOutput;
import com.example.api.services.pickup_point.dto.AddPickupPointInput;
import com.example.api.services.pickup_point.dto.GetListPickupPointOutput;
import com.example.api.services.pickup_point.dto.ManipulatePickupPointOutput;
import com.example.api.services.pickup_point.dto.UpdatePickupPointInput;
import com.example.shared.db.dto.GetListPickupPointDTO;
import com.example.shared.db.entities.Account;
import com.example.shared.db.entities.Bus;
import com.example.shared.db.entities.Employee;
import com.example.shared.db.entities.PickupPoint;
import com.example.shared.db.entities.Ride;
import com.example.shared.db.entities.StudentAssign;
import com.example.shared.db.repo.AccountRepository;
import com.example.shared.db.repo.BusRepository;
import com.example.shared.db.repo.EmployeeRepository;
import com.example.shared.db.repo.ParentRepository;
import com.example.shared.db.repo.PickupPointRepository;
import com.example.shared.db.repo.RidePickupPointRepository;
import com.example.shared.db.repo.RideRepository;
import com.example.shared.db.repo.RoutePickupPointRepository;
import com.example.shared.db.repo.StudentAssignRepository;
import com.example.shared.db.repo.StudentPickupPointRepository;
import com.example.shared.db.repo.StudentRepository;
import com.example.shared.enumeration.EmployeeRole;
import com.example.shared.enumeration.RideStatus;
import com.example.shared.exception.MyException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PickupPointServiceImpl implements PickupPointService {
    private final AccountRepository accountRepository;
    private final EmployeeRepository employeeRepository;
    private final PickupPointRepository pickupPointRepository;
    private final StudentPickupPointRepository studentPickupPointRepository;
    private final RidePickupPointRepository ridePickupPointRepository;
    private final RoutePickupPointRepository routePickupPointRepository;
    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final RideRepository rideRepository;
    private final BusRepository busRepository;
    private final StudentAssignRepository studentAssignRepository;

    @Override
    public Page<GetListPickupPointOutput> getListPickupPoint(PickupPointFilterParam filterParam,
                                                             Pageable pageable) {
        Page<PickupPoint> pickupPointPage = pickupPointRepository.getPagePickupPoint(
            filterParam.getAddress(), pageable);

        Page<GetListPickupPointOutput> result = pickupPointPage.map(GetListPickupPointOutput::fromEntity);
        // find all students and rides for each pickup point
        for (GetListPickupPointOutput output : result) {
            output.setStudents(
                studentRepository.findAllByPickupPointId(output.getPickupPoint().getId())
                    .stream().map(StudentOutput::fromEntity).toList()
            );
            output.setRides(
                rideRepository.findByPickupPointId(output.getPickupPoint().getId())
                    .stream().map(RideOutput::fromEntity).toList()
            );
        }

        // enrich assign student - bus: numberPlate
        for (GetListPickupPointOutput output : result) {
            for (StudentOutput student : output.getStudents()) {
                StudentAssign studentAssign = studentAssignRepository.findByStudentId(student.getId()).orElse(null);
                student.setNumberPlateAssign(studentAssign == null ? null : studentAssign.getNumberPlate());
            }
        }

        return result;
    }

    @Override
    @Transactional
    public void addPickupPoint(AddPickupPointInput input) {
        if (input.getAddress() == null || input.getLatitude() == null ||
            input.getLongitude() == null) {
            throw new MyException(
                null,
                "INVALID_INPUT",
                "Address, latitude, and longitude must not be null",
                HttpStatus.BAD_REQUEST
            );
        }

        if (pickupPointRepository.existsByAddress(input.getAddress())) {
            throw new MyException(
                null,
                "PICKUP_POINT_ALREADY_EXISTS",
                "Pickup point with address " + input.getAddress() + " already exists",
                HttpStatus.BAD_REQUEST
            );
        }

        PickupPoint pickupPoint = PickupPoint.builder()
            .address(input.getAddress())
            .latitude(input.getLatitude())
            .longitude(input.getLongitude())
            .build();

        pickupPointRepository.save(pickupPoint);
    }

    @Override
    @Transactional
    public void updatePickupPoint(UpdatePickupPointInput input) {
        if (input.getId() == null || input.getAddress() == null
            || input.getLatitude() == null || input.getLongitude() == null) {
            throw new MyException(
                null,
                "INVALID_INPUT",
                "Id, address, latitude, and longitude must not be null",
                HttpStatus.BAD_REQUEST
            );
        }

        PickupPoint pickupPoint = pickupPointRepository.findById(input.getId())
            .orElseThrow(() -> new MyException(
                null,
                "PICKUP_POINT_NOT_FOUND",
                "Pickup point with id " + input.getId() + " not found",
                HttpStatus.NOT_FOUND
            ));

        // check new address duplicate
        if (!pickupPoint.getAddress().equals(input.getAddress())
            && pickupPointRepository.existsByAddress(input.getAddress())) {
            throw new MyException(
                null,
                "PICKUP_POINT_ALREADY_EXISTS",
                "Pickup point with address " + input.getAddress() + " already exists",
                HttpStatus.BAD_REQUEST
            );
        }

        pickupPoint.setAddress(input.getAddress());
        pickupPoint.setLatitude(input.getLatitude());
        pickupPoint.setLongitude(input.getLongitude());

        pickupPointRepository.save(pickupPoint);
    }

    @Override
    @Transactional
    public void deletePickupPoint(Long id) {
        PickupPoint pickupPoint = pickupPointRepository.findById(id)
            .orElseThrow(() -> new MyException(
                null,
                "PICKUP_POINT_NOT_FOUND",
                "Pickup point with id " + id + " not found",
                HttpStatus.NOT_FOUND
            ));

        studentPickupPointRepository.deleteAllByPickupPoint(pickupPoint);
        ridePickupPointRepository.deleteAllByPickupPoint(pickupPoint);
        routePickupPointRepository.deleteAllByPickupPoint(pickupPoint);

        pickupPointRepository.delete(pickupPoint);
    }

    @Override
    public ManipulatePickupPointOutput getListManipulatePickupPoint(Account account, Instant date,
                                                                    Long rideId) {
        Employee driver;
        Employee driverMate;
        Bus bus;

        // find driver, driver mate, and bus
        Employee employee = employeeRepository.findByAccountId(account.getId())
            .orElseThrow(() -> new MyException(
                null,
                "EMPLOYEE_NOT_FOUND",
                "Employee with account id " + account.getId() + " not found",
                HttpStatus.NOT_FOUND
            ));

        if (employee.getRole().equals(EmployeeRole.DRIVER)) {
            driver = employee;
            bus = busRepository.findByDriverId(driver.getId())
                .orElseThrow(() -> new MyException(
                    null,
                    "BUS_NOT_FOUND",
                    "Bus with driver id " + driver.getId() + " not found",
                    HttpStatus.NOT_FOUND
                ));
            driverMate = employeeRepository.findById(bus.getDriverMateId())
                .orElseThrow(() -> new MyException(
                    null,
                    "EMPLOYEE_NOT_FOUND",
                    "Employee with id " + bus.getDriverMateId() + " not found",
                    HttpStatus.NOT_FOUND
                ));
        } else {
            driverMate = employee;
            bus = busRepository.findByDriverMateId(driverMate.getId())
                .orElseThrow(() -> new MyException(
                    null,
                    "BUS_NOT_FOUND",
                    "Bus with driver mate id " + driverMate.getId() + " not found",
                    HttpStatus.NOT_FOUND
                ));
            driver = employeeRepository.findById(bus.getDriverId())
                .orElseThrow(() -> new MyException(
                    null,
                    "EMPLOYEE_NOT_FOUND",
                    "Employee with id " + bus.getDriverId() + " not found",
                    HttpStatus.NOT_FOUND
                ));
        }

        Ride ride;
        if (rideId != null) {
            ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new MyException(
                    null,
                    "RIDE_NOT_FOUND",
                    "Ride with id " + rideId + " not found",
                    HttpStatus.NOT_FOUND
                ));


            // validate employee with ride
            if (!Objects.equals(ride.getBus().getDriverId(), driver.getId()) &&
                !Objects.equals(ride.getBus().getDriverMateId(), driverMate.getId())) {
                throw new MyException(
                    null,
                    "EMPLOYEE_NOT_IN_RIDE",
                    "Employee with id " + driver.getId() + " or " + driverMate.getId() +
                        " not in ride with id " + rideId,
                    HttpStatus.BAD_REQUEST
                );
            }
        } else {
            // find manipulate ride
            List<Ride> rides = rideRepository.findByBusIdAndNotInStatusAndStartAt(
                bus.getId(),
                RideStatus.FINISHED,
                date
            );
            // ride to school pending => get ride to school,
            // else get ride to home (just have 1 element in array)
            if (rides.isEmpty()) {
                return null;
            }
            if (rides.size() > 1 && !rides.get(0).getIsToSchool()) {
                ride = rides.get(1);
            } else {
                ride = rides.get(0);
            }
        }


        // find pickup points & corresponding students
        List<ManipulatePickupPointOutput.PickupPointWithStudent> pickupPointWithStudents = new ArrayList<>();
        // find pickup points by ride id
        List<PickupPoint> pickupPoints = pickupPointRepository.findByRideId(ride.getId());

        for (PickupPoint pickupPoint : pickupPoints) {
            // find students & student pickup points
            List<ManipulatePickupPointOutput.PickupPointWithStudent.StudentWithPickupPoint> studentWithPickupPoints = new ArrayList<>();
            List<StudentOutput> students = studentRepository.findAllByPickupPointId(pickupPoint.getId())
                .stream().map(StudentOutput::fromEntity).toList();
            for (StudentOutput student : students) {
                studentWithPickupPoints.add(
                    ManipulatePickupPointOutput.PickupPointWithStudent.StudentWithPickupPoint.builder()
                        .student(student)
                        .studentPickupPoint(
                            StudentPickupPointOutput.fromEntity(
                                studentPickupPointRepository.findByStudentIdAndPickupPointId(
                                    student.getId(),
                                    pickupPoint.getId()
                                ).orElseThrow(() -> new MyException(
                                    null,
                                    "STUDENT_PICKUP_POINT_NOT_FOUND",
                                    "Student pickup point with student id " + student.getId() +
                                        " and pickup point id " + pickupPoint.getId() + " not found",
                                    HttpStatus.NOT_FOUND
                                ))
                            )
                        )
                        .build()
                );

                // enrich student assign - bus with number plate
                StudentAssign studentAssign = studentAssignRepository.findByStudentId(student.getId()).orElse(null);
                student.setNumberPlateAssign(studentAssign == null ? null : studentAssign.getNumberPlate());
            }

            pickupPointWithStudents.add(
                ManipulatePickupPointOutput.PickupPointWithStudent.builder()
                    .pickupPoint(PickupPointOutput.fromEntity(pickupPoint))
                    .ridePickupPoint(
                        RidePickupPointOutput.fromEntity(
                            ridePickupPointRepository.findByRideIdAndPickupPointId(
                                ride.getId(),
                                pickupPoint.getId()
                            ).orElseThrow(() -> new MyException(
                                null,
                                "RIDE_PICKUP_POINT_NOT_FOUND",
                                "Ride pickup point with ride id " + ride.getId() +
                                    " and pickup point id " + pickupPoint.getId() + " not found",
                                HttpStatus.NOT_FOUND
                            ))
                        )
                    )
                    .studentWithPickupPoints(studentWithPickupPoints)
                    .build()
            );

        }

        return ManipulatePickupPointOutput.builder()
            .bus(BusOutput.fromEntity(bus))
            .driver(EmployeeOutput.fromEntity(driver))
            .driverMate(EmployeeOutput.fromEntity(driverMate))
            .ride(RideOutput.fromEntity(ride))
            .pickupPointWithStudents(pickupPointWithStudents)
            .build();
    }

    @Override
    public List<RideOutput> getListRideId(Account account, Instant date) {
        // find driver, driver mate, and bus
        Employee employee = employeeRepository.findByAccountId(account.getId())
            .orElseThrow(() -> new MyException(
                null,
                "EMPLOYEE_NOT_FOUND",
                "Employee with account id " + account.getId() + " not found",
                HttpStatus.NOT_FOUND
            ));

        return rideRepository.findEmployeeRides(employee.getId(), date)
            .stream().map(RideOutput::fromEntity).toList();
    }
}
