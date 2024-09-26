package com.example.api.scheduler;

import com.example.api.services.ride.RideService;
import com.example.api.services.ride.dto.UpsertRideInput;
import com.example.shared.db.entities.Ride;
import com.example.shared.db.repo.RidePickupPointRepository;
import com.example.shared.db.repo.RideRepository;
import com.example.shared.utils.DateConvertUtil;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideAutoCreateScheduler {
    private final RideRepository rideRepository;
    private final RidePickupPointRepository ridePickupPointRepository;

    private final RideService rideService;

    // schedule to create rides for the next day at 11:00 PM
    // it is copy all rides from the current day to the next day with the same pickup points and students
    @Scheduled(cron = "0 28 21 * * ?")
    @Transactional
    public void createRideForNextDay() {
        log.info("Start create ride for next day");

        // get all rides in today
        List<Ride> rides = rideRepository.findAllRidesByDateStartAt(Instant.now(), true);

        // create upsertRideInputs for the next day
        List<UpsertRideInput> upsertRideInputs = new ArrayList<>();
        for (Ride ride : rides) {
            List<Long> pickupPointIds = ridePickupPointRepository.findPickupPointIdsByRideId(ride.getId());

            UpsertRideInput upsertRideInput = UpsertRideInput.builder()
                .busId(ride.getBus().getId())
                .startAt(DateConvertUtil.addDays(ride.getStartAt(), 1))
                .endAt(null)
                .startFrom(ride.getStartFrom())
                .isToSchool(true)
                .pickupPointIds(pickupPointIds)
                .build();

            upsertRideInputs.add(upsertRideInput);
        }

        // create rides for the next day
        for (UpsertRideInput upsertRideInput : upsertRideInputs) {
            rideService.upsertRide(upsertRideInput);
        }

        log.info("End create ride for next day");
    }
}
