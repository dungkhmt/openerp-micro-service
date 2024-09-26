package com.example.api.services.history;

import com.example.api.services.common_dto.BusOutput;
import com.example.api.services.common_dto.EmployeeOutput;
import com.example.api.services.common_dto.RideOutput;
import com.example.api.services.common_dto.StudentOutput;
import com.example.api.services.history.dto.AdminHistoryRideFilterParam;
import com.example.api.services.history.dto.AdminHistoryRideOutput;
import com.example.api.services.history.dto.ClientHistoryRideFilterParam;
import com.example.api.services.history.dto.ClientHistoryRideOutput;
import com.example.api.services.history.dto.EmployeeHistoryRideFilterParam;
import com.example.api.services.history.dto.EmployeeHistoryRideOutput;
import com.example.shared.db.entities.Account;
import com.example.shared.db.entities.Bus;
import com.example.shared.db.entities.Employee;
import com.example.shared.db.entities.Parent;
import com.example.shared.db.entities.Ride;
import com.example.shared.db.entities.StudentPickupPointHistory;
import com.example.shared.db.repo.BusRepository;
import com.example.shared.db.repo.EmployeeRepository;
import com.example.shared.db.repo.ParentRepository;
import com.example.shared.db.repo.PickupPointRepository;
import com.example.shared.db.repo.RideHistoryRepository;
import com.example.shared.db.repo.RidePickupPointHistoryRepository;
import com.example.shared.db.repo.RideRepository;
import com.example.shared.db.repo.StudentPickupPointHistoryRepository;
import com.example.shared.db.repo.StudentRepository;
import com.example.shared.exception.MyException;
import com.example.shared.utils.DateConvertUtil;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {
    private final RideRepository rideRepository;
    private final BusRepository busRepository;
    private final PickupPointRepository pickupPointRepository;
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final EmployeeRepository employeeRepository;
    private final StudentPickupPointHistoryRepository studentPickupPointHistoryRepository;
    private final RideHistoryRepository rideHistoryRepository;
    private final RidePickupPointHistoryRepository ridePickupPointHistoryRepository;

    @Override
    public Page<AdminHistoryRideOutput> getAdminHistoryRides(
        AdminHistoryRideFilterParam filterParam, Pageable pageable) {
        Page<AdminHistoryRideOutput> result = null;

        // set for query not get error
        boolean isAllDate = false;
        if (filterParam.getStartAt() == null || filterParam.getStartAt().isBlank()) {
            isAllDate = true;
            filterParam.setStartAt(DateConvertUtil.convertInstantToString(Instant.now()));
        }

        // find ride page
        Page<Ride> ridepage;
        if (filterParam.getAddress() != null || filterParam.getStudentPhoneNumber() != null
            || filterParam.getParentPhoneNumber() != null) {
            ridepage = rideRepository.searchHistory(
                DateConvertUtil.convertStringToInstant(filterParam.getStartAt()), filterParam.getRideId(),
                filterParam.getNumberPlate(), filterParam.getStatus(), filterParam.getIsToSchool(),
                filterParam.getAddress(), filterParam.getStudentPhoneNumber(),
                filterParam.getParentPhoneNumber(),
                isAllDate,
                pageable
            );
        } else {
            ridepage = rideRepository.searchHistory(
                DateConvertUtil.convertStringToInstant(filterParam.getStartAt()), filterParam.getRideId(),
                filterParam.getNumberPlate(), filterParam.getStatus(), filterParam.getIsToSchool(),
                isAllDate,
                pageable
            );
        }


        // map to output
        result = ridepage.map(ride -> {
            Bus bus = busRepository.findById(ride.getBus().getId())
                .orElseThrow(() -> new MyException(
                    null, "BUS_NOT_FOUND", "Bus not found", HttpStatus.NOT_FOUND
                ));
            return AdminHistoryRideOutput.builder()
                .bus(BusOutput.fromEntity(bus))
                .ride(RideOutput.fromEntity(ride))
                .driver(
                    bus.getDriverId() == null ? null :
                    EmployeeOutput.fromEntity(
                        employeeRepository.findById(bus.getDriverId())
                            .orElseThrow(() -> new MyException(
                                null, "EMPLOYEE_NOT_FOUND", "Employee not found",
                                HttpStatus.NOT_FOUND
                            ))
                    )
                )
                .driverMate(
                    bus.getDriverMateId() == null ? null :
                    EmployeeOutput.fromEntity(
                        employeeRepository.findById(bus.getDriverMateId())
                            .orElseThrow(() -> new MyException(
                                null, "EMPLOYEE_NOT_FOUND", "Employee not found",
                                HttpStatus.NOT_FOUND
                            ))
                    )
                )
                .rideHistories(rideHistoryRepository.findByRideId(ride.getId()))
                .ridePickupPointHistories(
                    ridePickupPointHistoryRepository.findByRideId(ride.getId())
                )
                .studentRideHistories(
                    studentPickupPointHistoryRepository.findByRideId(ride.getId()).stream()
                        .collect(Collectors.groupingBy(
                            StudentPickupPointHistory::getStudentId
                        ))
                        .keySet().stream()
                        .map(
                            studentPickupPointHistories -> AdminHistoryRideOutput.StudentRideHistory.builder()
                                .student(StudentOutput.fromEntity(
                                    studentRepository.findById(studentPickupPointHistories)
                                        .orElseThrow(() -> new MyException(
                                            null, "STUDENT_NOT_FOUND", "Student not found",
                                            HttpStatus.NOT_FOUND
                                        ))
                                ))
                                .studentPickupPointHistories(
                                    studentPickupPointHistoryRepository.findByRideIdAndStudentId(
                                        ride.getId(), studentPickupPointHistories
                                    )
                                )
                                .build()
                        )
                        .collect(Collectors.toList())
                )
                .build();
        });

        return result;
    }

    @Override
    public Page<EmployeeHistoryRideOutput> getEmployeeHistoryRides(
        EmployeeHistoryRideFilterParam filterParam, Pageable pageable, Account account) {
        Employee employee = employeeRepository.findByAccountId(account.getId())
            .orElseThrow(() -> new MyException(
                null, "EMPLOYEE_NOT_FOUND", "Employee not found", HttpStatus.NOT_FOUND
            ));

        Page<EmployeeHistoryRideOutput> result = null;

        // set for query not get error
        // set for query not get error
        boolean isAllDate = false;
        if (filterParam.getStartAt() == null || filterParam.getStartAt().isBlank()) {
            isAllDate = true;
            filterParam.setStartAt(DateConvertUtil.convertInstantToString(Instant.now()));
        }

        // find ride page
        Page<Ride> ridepage;
        if (filterParam.getAddress() != null || filterParam.getStudentPhoneNumber() != null
            || filterParam.getParentPhoneNumber() != null) {
            ridepage = rideRepository.searchEmployeeHistory(
                DateConvertUtil.convertStringToInstant(filterParam.getStartAt()),
                filterParam.getRideId(),
                filterParam.getNumberPlate(),
                filterParam.getStatus(),
                filterParam.getIsToSchool(),
                filterParam.getAddress(),
                filterParam.getStudentPhoneNumber(),
                filterParam.getParentPhoneNumber(),
                isAllDate, employee.getId(),
                pageable
            );
        } else {
            ridepage = rideRepository.searchEmployeeHistory(
                DateConvertUtil.convertStringToInstant(filterParam.getStartAt()),
                filterParam.getRideId(),
                filterParam.getNumberPlate(),
                filterParam.getStatus(),
                filterParam.getIsToSchool(),
                isAllDate,
                employee.getId(),
                pageable
            );
        }


        // map to output
        result = ridepage.map(ride -> {
            Bus bus = busRepository.findById(ride.getBus().getId())
                .orElseThrow(() -> new MyException(
                    null, "BUS_NOT_FOUND", "Bus not found", HttpStatus.NOT_FOUND
                ));
            return EmployeeHistoryRideOutput.builder()
                .bus(BusOutput.fromEntity(bus))
                .ride(RideOutput.fromEntity(ride))
                .driver(
                    bus.getDriverId() == null ? null :
                    EmployeeOutput.fromEntity(
                        employeeRepository.findById(bus.getDriverId())
                            .orElseThrow(() -> new MyException(
                                null, "EMPLOYEE_NOT_FOUND", "Employee not found",
                                HttpStatus.NOT_FOUND
                            ))
                    )
                )
                .driverMate(
                    bus.getDriverMateId() == null ? null :
                    EmployeeOutput.fromEntity(
                        employeeRepository.findById(bus.getDriverMateId())
                            .orElseThrow(() -> new MyException(
                                null, "EMPLOYEE_NOT_FOUND", "Employee not found",
                                HttpStatus.NOT_FOUND
                            ))
                    )
                )
                .rideHistories(rideHistoryRepository.findByRideId(ride.getId()))
                .ridePickupPointHistories(
                    ridePickupPointHistoryRepository.findByRideId(ride.getId())
                )
                .studentRideHistories(
                    studentPickupPointHistoryRepository.findByRideId(ride.getId()).stream()
                        .collect(Collectors.groupingBy(
                            StudentPickupPointHistory::getStudentId
                        ))
                        .keySet().stream()
                        .map(
                            studentPickupPointHistories -> EmployeeHistoryRideOutput.StudentRideHistory.builder()
                                .student(StudentOutput.fromEntity(
                                    studentRepository.findById(studentPickupPointHistories)
                                        .orElseThrow(() -> new MyException(
                                            null, "STUDENT_NOT_FOUND", "Student not found",
                                            HttpStatus.NOT_FOUND
                                        ))
                                ))
                                .studentPickupPointHistories(
                                    studentPickupPointHistoryRepository.findByRideIdAndStudentId(
                                        ride.getId(), studentPickupPointHistories
                                    )
                                )
                                .build()
                        )
                        .collect(Collectors.toList())
                )
                .build();
        });

        return result;
    }

    @Override
    public Page<ClientHistoryRideOutput> getClientHistoryRides(
        ClientHistoryRideFilterParam filterParam, Pageable pageable, Account account) {
        Page<ClientHistoryRideOutput> result = null;
        Parent parent = parentRepository.findByAccountId(account.getId())
            .orElseThrow(() -> new MyException(
                null, "PARENT_NOT_FOUND", "Parent not found", HttpStatus.NOT_FOUND
            ));
        List<Long> studentIds = studentRepository.findAllByParentIdAndStudentPhoneNumber(
            parent.getId(), filterParam.getStudentPhoneNumber()
            ).stream()
            .map(StudentOutput::fromEntity)
            .map(StudentOutput::getId)
            .toList();

        // set for query not get error
        boolean isAllDate = false;
        if (filterParam.getStartAt() == null || filterParam.getStartAt().isBlank()) {
            isAllDate = true;
            filterParam.setStartAt(DateConvertUtil.convertInstantToString(Instant.now()));
        }

        // find ride page
        Page<Ride> ridepage = rideRepository.searchClientHistory(
            DateConvertUtil.convertStringToInstant(filterParam.getStartAt()),
            filterParam.getRideId(),
            filterParam.getNumberPlate(),
            filterParam.getStatus(),
            filterParam.getIsToSchool(),
            filterParam.getAddress(),
            isAllDate,
            studentIds,
            pageable
        );

        // map to output
        result = ridepage.map(ride -> {
            Bus bus = busRepository.findById(ride.getBus().getId())
                .orElseThrow(() -> new MyException(
                    null, "BUS_NOT_FOUND", "Bus not found", HttpStatus.NOT_FOUND
                ));
            return ClientHistoryRideOutput.builder()
                .bus(BusOutput.fromEntity(bus))
                .ride(RideOutput.fromEntity(ride))
                .driver(
                    bus.getDriverId() == null ? null :
                    EmployeeOutput.fromEntity(
                        employeeRepository.findById(bus.getDriverId())
                            .orElseThrow(() -> new MyException(
                                null, "EMPLOYEE_NOT_FOUND", "Employee not found",
                                HttpStatus.NOT_FOUND
                            ))
                    )
                )
                .driverMate(
                    bus.getDriverMateId() == null ? null :
                    EmployeeOutput.fromEntity(
                        employeeRepository.findById(bus.getDriverMateId())
                            .orElseThrow(() -> new MyException(
                                null, "EMPLOYEE_NOT_FOUND", "Employee not found",
                                HttpStatus.NOT_FOUND
                            ))
                    )
                )
                .rideHistories(rideHistoryRepository.findByRideId(ride.getId()))
                .ridePickupPointHistories(
                    ridePickupPointHistoryRepository.findByRideId(ride.getId())
                )
                .studentRideHistories(
                    studentPickupPointHistoryRepository.findByRideId(ride.getId()).stream()
                        .collect(Collectors.groupingBy(
                            StudentPickupPointHistory::getStudentId
                        ))
                        .keySet().stream()
                        .map(
                            studentPickupPointHistories -> ClientHistoryRideOutput.StudentRideHistory.builder()
                                .student(StudentOutput.fromEntity(
                                    studentRepository.findById(studentPickupPointHistories)
                                        .orElseThrow(() -> new MyException(
                                            null, "STUDENT_NOT_FOUND", "Student not found",
                                            HttpStatus.NOT_FOUND
                                        ))
                                ))
                                .studentPickupPointHistories(
                                    studentPickupPointHistoryRepository.findByRideIdAndStudentId(
                                        ride.getId(), studentPickupPointHistories
                                    )
                                )
                                .build()
                        )
                        .collect(Collectors.toList())
                )
                .build();
        });

        return result;
    }
}
