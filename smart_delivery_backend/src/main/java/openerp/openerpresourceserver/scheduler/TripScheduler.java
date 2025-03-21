package openerp.openerpresourceserver.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.service.VehicleScheduleService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Scheduler responsible for automatically generating trips based on vehicle schedules.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TripScheduler {

    private final VehicleScheduleService vehicleScheduleService;

    /**
     * Daily at 1:00 AM, generate trips for the next day based on schedules
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void generateTripsForTomorrow() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        DayOfWeek tomorrowDay = tomorrow.getDayOfWeek();

        log.info("Generating trips for tomorrow ({})...", tomorrow);

        int tripsGenerated = vehicleScheduleService.generateTripsForDate(tomorrowDay);

        log.info("Generated {} trips for {}", tripsGenerated, tomorrow);
    }

    /**
     * Every Monday at 1:30 AM, generate trips for the whole week
     */
    @Scheduled(cron = "0 30 1 * * MON")
    public void generateTripsForWeek() {
        log.info("Generating trips for the upcoming week...");

        int totalTrips = 0;
        LocalDate today = LocalDate.now();

        // Generate trips for each day of the week (Monday to Sunday)
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.plusDays(i);
            DayOfWeek dayOfWeek = date.getDayOfWeek();

            log.info("Generating trips for {} ({})", date, dayOfWeek);
            int tripsGenerated = vehicleScheduleService.generateTripsForDate(dayOfWeek);
            totalTrips += tripsGenerated;

            log.info("Generated {} trips for {}", tripsGenerated, date);
        }

        log.info("Total trips generated for the week: {}", totalTrips);
    }
}