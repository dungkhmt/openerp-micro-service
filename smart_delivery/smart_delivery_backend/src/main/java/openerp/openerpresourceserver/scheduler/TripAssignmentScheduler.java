//package openerp.openerpresourceserver.scheduler;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import openerp.openerpresourceserver.entity.Hub;
//import openerp.openerpresourceserver.repository.HubRepo;
//import openerp.openerpresourceserver.service.TripAssignmentService;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
///**
// * Scheduler to run trip assignments at specific times of day
// */
//@Component
//@Slf4j
//@EnableScheduling
//@RequiredArgsConstructor
//public class TripAssignmentScheduler {
//
//    private final TripAssignmentService tripAssignmentService;
//    private final HubRepo hubRepo;
//
//    /**
//     * Schedule morning trip assignments at 8:00 AM every day
//     */
//    @Scheduled(cron = "0 0 8 * * ?")
//    public void scheduleMorningAssignments() {
//        log.info("Running morning trip assignments at {}", LocalDateTime.now());
//        runAssignmentsForAllHubs(true);
//    }
//
//    /**
//     * Schedule evening trip assignments at 5:00 PM every day
//     */
//    @Scheduled(cron = "0 0 17 * * ?")
//    public void scheduleEveningAssignments() {
//        log.info("Running evening trip assignments at {}", LocalDateTime.now());
//        runAssignmentsForAllHubs(false);
//    }
//
//    /**
//     * Run assignments for all active hubs
//     * @param isMorning true for morning assignments, false for evening
//     */
//    private void runAssignmentsForAllHubs(boolean isMorning) {
//        List<Hub> hubs = hubRepo.findAll();
//
//        int totalAssigned = 0;
//        for (Hub hub : hubs) {
//            try {
//                int assigned = isMorning
//                        ? tripAssignmentService.assignMorningTrips(hub.getHubId())
//                        : tripAssignmentService.assignEveningTrips(hub.getHubId());
//
//                log.info("Assigned {} orders for hub {} ({})",
//                        assigned, hub.getName(), isMorning ? "morning" : "evening");
//
//                totalAssigned += assigned;
//            } catch (Exception e) {
//                log.error("Error assigning trips for hub {}: {}", hub.getName(), e.getMessage(), e);
//            }
//        }
//
//        log.info("Completed {} assignments, total assigned: {}",
//                isMorning ? "morning" : "evening", totalAssigned);
//    }
//}