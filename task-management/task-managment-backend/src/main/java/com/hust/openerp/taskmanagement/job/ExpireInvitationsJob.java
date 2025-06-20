package com.hust.openerp.taskmanagement.job;

import com.hust.openerp.taskmanagement.entity.OrganizationInvitation;
import com.hust.openerp.taskmanagement.model.OrgInvitationStatusEnum;
import com.hust.openerp.taskmanagement.repository.OrganizationInvitationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ExpireInvitationsJob {
    private static final Logger LOG = LoggerFactory.getLogger(ExpireInvitationsJob.class);

    private final OrganizationInvitationRepository invitationRepository;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

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

    @Scheduled(cron = "${app.job.cron.expire-invitations-expression:0 0 * * * *}")
    public void expireInvitations() {
        LOG.info("Running ExpireInvitationsJob...");

        executor.submit(() -> {
            try {
                Date now = new Date();
                List<OrganizationInvitation> expired = invitationRepository
                    .findByStatusIdAndExpirationTimeBefore(OrgInvitationStatusEnum.PENDING.getStatusId(), now);

                if (expired.isEmpty()) {
                    LOG.info("No invitations to expire.");
                    return;
                }

                expired.forEach(invite -> invite.setStatusId(OrgInvitationStatusEnum.EXPIRED.getStatusId()));
                invitationRepository.saveAll(expired);

                LOG.info("Expired {} invitations", expired.size());
            } catch (Exception e) {
                LOG.error("Error expiring invitations", e);
            }
        });
    }
}
