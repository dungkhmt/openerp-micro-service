package com.hust.openerp.taskmanagement.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hust.openerp.taskmanagement.config.AppConfig;
import com.hust.openerp.taskmanagement.entity.Task;
import com.hust.openerp.taskmanagement.repository.TaskRepository;
import com.hust.openerp.taskmanagement.repository.UserRepository;
import com.hust.openerp.taskmanagement.service.NotificationService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RemindDueTasksJob {
    private static final Logger LOG = LoggerFactory.getLogger(RemindDueTasksJob.class);

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    private final NotificationService notificationService;

    private final AppConfig appConfig;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @PostConstruct
    private void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOG.error(e.toString());
            }
        }));
    }

    //@Scheduled(cron = "${app.job.cron.remind-due-tasks-expression:-}")
    public void remindDueTasks() {
        LOG.info("Remind due tasks job every day at 8:00 AM");

        var dueTasks = taskRepository.getTasksDueDateIntervalDay(1);

        // group tasks by assignee to concurrent hash map
        var tasksByAssignee = dueTasks.stream()
                .collect(Collectors.groupingByConcurrent(Task::getAssigneeId));

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        tasksByAssignee.forEach((assigneeId, tasks) -> {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                sendReminderEmail(assigneeId, tasks);
            }, executor);
            futures.add(future);
        });

        // Chờ cho tất cả CompletableFuture hoàn thành
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void sendReminderEmail(String assigneeId, List<Task> tasks) {
        if (tasks.isEmpty()) {
            return;
        }

        var assignee = userRepository.findById(assigneeId).orElse(null);

        if (assignee == null || assignee.getEmail() == null) {
            return;
        }
        LOG.debug("Send mail to {} - {}", assignee.getId(), assignee.getEmail());

        try {
            var subject = "Bạn có " + tasks.size() + " nhiệm vụ sẽ đến hạn vào ngày mai!";
            Map<String, Object> model = new HashMap<>();
            model.put("tasks", tasks);
            model.put("name", assignee.getFirstName() + " " + assignee.getLastName());
            model.put("appUrl", appConfig.getClientUrl());

            notificationService.createMailNotification(null, assignee.getEmail(), subject, "remind-due-task", model);
        } catch (Exception e) {
            LOG.error("Error send remind mail", e);
        }
    }
}
