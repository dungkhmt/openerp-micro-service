package com.example.api.services.ride;

import com.example.api.services.ride.dto.UpdateRideEmployeeInput;
import com.example.api.services.ride.dto.UpsertRideInput;
import com.example.api.services.ride.dto.UpdateRideInput;
import com.example.shared.db.entities.Account;
import com.example.shared.db.entities.Bus;
import com.example.shared.db.entities.Employee;
import com.example.shared.db.entities.PickupPoint;
import com.example.shared.db.entities.Ride;
import com.example.shared.db.entities.RidePickupPoint;
import com.example.shared.db.entities.RidePickupPointHistory;
import com.example.shared.db.entities.StudentPickupPoint;
import com.example.shared.db.entities.StudentPickupPointHistory;
import com.example.shared.db.repo.BusRepository;
import com.example.shared.db.repo.EmployeeRepository;
import com.example.shared.db.repo.PickupPointRepository;
import com.example.shared.db.repo.RideHistoryRepository;
import com.example.shared.db.repo.RidePickupPointHistoryRepository;
import com.example.shared.db.repo.RidePickupPointRepository;
import com.example.shared.db.repo.RideRepository;
import com.example.shared.db.repo.StudentPickupPointHistoryRepository;
import com.example.shared.db.repo.StudentPickupPointRepository;
import com.example.shared.enumeration.BusStatus;
import com.example.shared.enumeration.RidePickupPointStatus;
import com.example.shared.enumeration.RideStatus;
import com.example.shared.enumeration.StudentPickupPointStatus;
import com.example.shared.exception.MyException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {
    private final EmployeeRepository employeeRepository;
    private final RideRepository rideRepository;
    private final RidePickupPointRepository ridePickupPointRepository;
    private final BusRepository busRepository;
    private final PickupPointRepository pickupPointRepository;
    private final StudentPickupPointRepository studentPickupPointRepository;
    private final StudentPickupPointHistoryRepository studentPickupPointHistoryRepository;
    private final RideHistoryRepository rideHistoryRepository;
    private final RidePickupPointHistoryRepository ridePickupPointHistoryRepository;

    @Override
    @Transactional
    public void upsertRide(UpsertRideInput upsertRideInput) {
        boolean isAdd = upsertRideInput.getId() == null;

        // validate input
        if (upsertRideInput.getBusId() == null || upsertRideInput.getStartFrom() == null ||
            upsertRideInput.getStartAt() == null || upsertRideInput.getPickupPointIds() == null) {
            throw new MyException(
                null,
                "missing_required_fields",
                "Missing required fields",
                HttpStatus.BAD_REQUEST
            );
        }
        if (upsertRideInput.getPickupPointIds().isEmpty()) {
            throw new MyException(
                null,
                "pickup_point_required",
                "Pickup points are required",
                HttpStatus.BAD_REQUEST
            );
        }
        if (upsertRideInput.getEndAt() != null && upsertRideInput.getStartAt().isAfter(
            upsertRideInput.getEndAt())) {
            throw new MyException(
                null,
                "invalid_time",
                "Start time must be before end time",
                HttpStatus.BAD_REQUEST
            );
        }
        // validate bus
        Bus bus = busRepository.findById(upsertRideInput.getBusId())
                .orElseThrow(() -> new MyException(
                    null,
                    "bus_not_found",
                    "Bus not found with id: " + upsertRideInput.getBusId(),
                    HttpStatus.BAD_REQUEST
                ));

        // validate pickup points
        List<PickupPoint> pickupPoints = pickupPointRepository.findAllById(upsertRideInput.getPickupPointIds());
        if (pickupPoints.size() != upsertRideInput.getPickupPointIds().size()) {
            throw new MyException(
                null,
                "pickup_point_not_found",
                "Some pickup points not found",
                HttpStatus.BAD_REQUEST
            );
        }
        // sort to the right order from request
        pickupPoints.sort(Comparator.comparing(pickupPoint -> upsertRideInput.getPickupPointIds().indexOf(pickupPoint.getId())));

        // upsert ride
        Ride ride;
        if (upsertRideInput.getId() == null) {
            ride = rideRepository.save(
                Ride.builder()
                    .bus(bus)
                    .startAt(upsertRideInput.getStartAt())
                    .endAt(upsertRideInput.getEndAt())
                    .startFrom(upsertRideInput.getStartFrom())
                    .status(RideStatus.PENDING)
                    .isToSchool(upsertRideInput.getIsToSchool())
                    .build()
            );
            upsertRideInput.setId(ride.getId());
        } else {
            ride = rideRepository.findById(upsertRideInput.getId())
                .orElseThrow(() -> new MyException(
                    null,
                    "ride_not_found",
                    "Ride not found with id: " + upsertRideInput.getId(),
                    HttpStatus.BAD_REQUEST
                ));
            ride.setBus(bus);
            ride.setStartAt(upsertRideInput.getStartAt());
            ride.setEndAt(upsertRideInput.getEndAt());
            ride.setStartFrom(upsertRideInput.getStartFrom());
            ride.setStatus(RideStatus.PENDING);
            ride.setIsToSchool(upsertRideInput.getIsToSchool());
            rideRepository.save(ride);
        }


        rideHistoryRepository.save(ride.toRideHistory(
            bus.getDriverId(),
            bus.getDriverMateId()
        ));

        // save ride pickup points
        if (!isAdd) {
            ridePickupPointRepository.deleteAllByRideId(upsertRideInput.getId());
            ridePickupPointHistoryRepository.deleteAllByRideId(upsertRideInput.getId());
        }
        pickupPoints.forEach(pickupPoint -> {
            RidePickupPoint ridePickupPoint = ridePickupPointRepository.save(
                RidePickupPoint.builder()
                    .ride(ride)
                    .pickupPoint(pickupPoint)
                    .orderIndex(pickupPoints.indexOf(pickupPoint))
                    .status(RidePickupPointStatus.PICKING)
                    .build()
            );
            ridePickupPointHistoryRepository.save(
                RidePickupPointHistory.builder()
                    .ridePickupPointId(ridePickupPoint.getId())
                    .rideId(ride.getId())
                    .pickupPointId(pickupPoint.getId())
                    .orderIndex(pickupPoints.indexOf(pickupPoint))
                    .status(RidePickupPointStatus.PICKING)
                    .address(pickupPoint.getAddress())
                    .latitude(pickupPoint.getLatitude())
                    .longitude(pickupPoint.getLongitude())
                    .build()
            );
        });

        // default upsert back to school ride is reversed from school ride
        if (upsertRideInput.getIsToSchool() && isAdd) {
            upsertRideInput.setId(null);
            upsertRideInput.setIsToSchool(false);
            upsertRideInput.setStartFrom("School");
            // always start at 17:45pm, not related to the input
            ZonedDateTime startAtZonedDateTime = upsertRideInput.getStartAt().atZone(ZoneId.systemDefault());
            ZonedDateTime newStartAtZonedDateTime = startAtZonedDateTime.withHour(17).withMinute(45);
            upsertRideInput.setStartAt(newStartAtZonedDateTime.toInstant());
            upsertRideInput.setEndAt(null);
            Collections.reverse(upsertRideInput.getPickupPointIds());

            upsertRide(upsertRideInput);
        } else if (upsertRideInput.getIsToSchool() && !isAdd) { // update reversed ride in case wanna update ride to school
            long reversedRideId = upsertRideInput.getId() + 1; // note that the reversed ride id is always the next id of the current ride
            Ride reversedRide = rideRepository.findById(reversedRideId)
                .orElseThrow(() -> new MyException(
                    null,
                    "reversed_ride_not_found",
                    "Reversed ride not found with id: " + reversedRideId,
                    HttpStatus.BAD_REQUEST
                ));

            // compared if date of upsert input is different from the reversed ride => make the reversed ride the same date as the upsert input but still maintain the old time
            if (!(upsertRideInput.getStartAt().toEpochMilli() / 86400000L ==
                reversedRide.getStartAt().toEpochMilli() / 86400000L)) {
                ZonedDateTime startAtZonedDateTime = upsertRideInput.getStartAt().atZone(ZoneId.systemDefault());
                ZonedDateTime newStartAtZonedDateTime = startAtZonedDateTime.withHour(reversedRide.getStartAt().atZone(ZoneId.systemDefault()).getHour())
                    .withMinute(reversedRide.getStartAt().atZone(ZoneId.systemDefault()).getMinute());
                reversedRide.setStartAt(newStartAtZonedDateTime.toInstant());
                rideRepository.save(reversedRide);
            }
        }

        // set all student pickup points to PICKING status
        List<StudentPickupPoint> studentPickupPoints = studentPickupPointRepository
            .findByPickupPointIdIn(upsertRideInput.getPickupPointIds());
        studentPickupPoints.forEach(studentPickupPoint -> studentPickupPoint.setStatus(StudentPickupPointStatus.PICKING));
        studentPickupPointRepository.saveAll(studentPickupPoints);
        studentPickupPointHistoryRepository.saveAll(
            studentPickupPoints.stream()
                .map(studentPickupPoint -> new StudentPickupPointHistory(
                    studentPickupPoint,
                    studentPickupPoint.getPickupPoint().getAddress(),
                    studentPickupPoint.getPickupPoint().getLatitude(),
                    studentPickupPoint.getPickupPoint().getLongitude(),
                    ride.getId()
                ))
                .collect(Collectors.toList())
        );
    }

    @Override
    @Transactional
    public void updateRide(UpdateRideInput updateRideInput) {
        // validate ride
        Ride ride = rideRepository.findById(updateRideInput.getId())
                .orElseThrow(() -> new MyException(
                    null,
                    "ride_not_found",
                    "Ride not found with id: " + updateRideInput.getId(),
                    HttpStatus.BAD_REQUEST
                ));

        // validate bus
        Bus bus = busRepository.findById(updateRideInput.getBusId())
                .orElseThrow(() -> new MyException(
                    null,
                    "bus_not_found",
                    "Bus not found with id: " + updateRideInput.getBusId(),
                    HttpStatus.BAD_REQUEST
                ));

        // update ride
        ride.setBus(bus);
        ride.setStartAt(updateRideInput.getStartAt());
        ride.setEndAt(updateRideInput.getEndAt());
        ride.setStartFrom(updateRideInput.getStartFrom());
        ride.setStatus(updateRideInput.getStatus());
        rideRepository.save(ride);
        rideHistoryRepository.save(ride.toRideHistory(
            bus.getDriverId(),
            bus.getDriverMateId()
        ));
    }

    @Override
    @Transactional
    public void updateRideEmployee(UpdateRideEmployeeInput updateRideEmployeeInput,
                                   Account account) {
        Employee employee = employeeRepository.findByAccountId(account.getId())
                .orElseThrow(() -> new MyException(
                    null,
                    "employee_not_found",
                    "Employee not found with account id: " + account.getId(),
                    HttpStatus.BAD_REQUEST
                ));

        // find by ride id
        Ride ride = rideRepository.findById(updateRideEmployeeInput.getRideId())
                .orElseThrow(() -> new MyException(
                    null,
                    "ride_not_found",
                    "Ride not found with id: " + updateRideEmployeeInput.getRideId(),
                    HttpStatus.BAD_REQUEST
                ));

        // validate employee assign to ride
        Bus bus = ride.getBus();
        if (!bus.getDriverId().equals(employee.getId()) &&
            !bus.getDriverMateId().equals(employee.getId())) {
            throw new MyException(
                null,
                "employee_not_assign_to_ride",
                "Employee not assign to ride",
                HttpStatus.BAD_REQUEST
            );
        }

        // validate ride status
        if (updateRideEmployeeInput.getStatus() == RideStatus.RUNNING &&
            bus.getStatus() != BusStatus.RUNNING) {
            throw new MyException(
                null,
                "bus_not_running",
                "Bus is not running",
                HttpStatus.BAD_REQUEST
            );
        }

        // validate all student pickup points status updated when ride status is FINISHED
        if (updateRideEmployeeInput.getStatus() == RideStatus.FINISHED) {
            List<StudentPickupPointHistory> studentPickupPointHistories =
                studentPickupPointHistoryRepository.findByRideId(ride.getId());
            // history is track status of student pickup point from the beginning of the ride and a student pickup point can have multiple status in a ride
            // check last status of each student pickup point is AT_SCHOOL or AT_HOME => can update ride status to FINISHED

            // first get all last status of each student pickup point
            List<StudentPickupPointHistory> lastStudentPickupPointHistories =
                studentPickupPointHistories.stream()
                .collect(Collectors.groupingBy(StudentPickupPointHistory::getStudentPickupPointId))
                .values()
                .stream()
                .map(studentPickupPointHistoryList -> studentPickupPointHistoryList.stream()
                    .max(Comparator.comparing(StudentPickupPointHistory::getCreatedAt))
                    .orElseThrow(() -> new MyException(
                        null,
                        "student_pickup_point_history_not_found",
                        "Student pickup point history not found",
                        HttpStatus.BAD_REQUEST
                    ))
                )
                .toList();

            if (lastStudentPickupPointHistories.isEmpty()) {
                throw new MyException(
                    null,
                    "student_pickup_point_history_not_found",
                    "Student pickup point history not found",
                    HttpStatus.BAD_REQUEST
                );
            }

            // check all last status of each student pickup point is AT_SCHOOL or AT_HOME
            for (StudentPickupPointHistory studentPickupPointHistory : lastStudentPickupPointHistories) {
                if (studentPickupPointHistory.getStatus() != StudentPickupPointStatus.AT_SCHOOL &&
                    studentPickupPointHistory.getStatus() != StudentPickupPointStatus.AT_HOME) {
                    throw new MyException(
                        null,
                        "student_pickup_point_not_finished",
                        "Student pickup point not finished",
                        HttpStatus.BAD_REQUEST
                    );
                }
            }

            // update end time of ride
            ride.setEndAt(ZonedDateTime.now().toInstant());
        }

        // update ride status
        ride.setStatus(updateRideEmployeeInput.getStatus());
        rideRepository.save(ride);
    }
}
