package com.hust.openerp.taskmanagement.job;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hust.openerp.taskmanagement.service.MeetingPlanService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MeetingPlanStatusScheduler {
	private static final Logger LOG = LoggerFactory.getLogger(MeetingPlanStatusScheduler.class);

	private final MeetingPlanService meetingPlanService;
	private final ExecutorService executor = Executors.newFixedThreadPool(3);

	@PostConstruct
	private void init() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			executor.shutdown();
			try {
				if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
					executor.shutdownNow();
				}
			} catch (InterruptedException e) {
				LOG.error("Executor shutdown interrupted", e);
				executor.shutdownNow();
			}
		}));
	}

	@Scheduled(cron = "${app.job.cron.meeting-plan-status-update-expression:0 */5 * * * *}")
	public void updateMeetingPlanStatuses() {
		LOG.info("Running scheduled task to update meeting plan statuses");

		CompletableFuture<Void> closeRegistrations = CompletableFuture.runAsync(() -> {
			try {
				meetingPlanService.closeRegistrations(new Date());
			} catch (Exception e) {
				LOG.error("Error while closing registrations", e);
			}
		}, executor);

		CompletableFuture<Void> assignMeetingPlans = CompletableFuture.runAsync(() -> {
			try {
				meetingPlanService.assignMeetingPlans(new Date());
			} catch (Exception e) {
				LOG.error("Error while assigning meeting plans", e);
			}
		}, executor);

		CompletableFuture<Void> completeMeetingPlans = CompletableFuture.runAsync(() -> {
			try {
				meetingPlanService.completeMeetingPlans(new Date());
			} catch (Exception e) {
				LOG.error("Error while completing meeting plans", e);
			}
		}, executor);

		CompletableFuture.allOf(closeRegistrations, assignMeetingPlans, completeMeetingPlans).join();

		LOG.info("Scheduled task completed.");
	}

}
