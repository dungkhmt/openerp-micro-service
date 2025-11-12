package com.real_estate.post.runner;

import com.real_estate.post.models.DashboardEntity;
import com.real_estate.post.services.DashboardService;
import com.real_estate.post.services.PostSellService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Profile("start-calculate-pricePerM2")
public class StartCalculatePricePerM2 implements CommandLineRunner {
    @Value("${dashboard.duration}")
    Long DURATION;

    private final Logger logger = LoggerFactory.getLogger(StartCalculatePricePerM2.class);

    @Autowired
    DashboardService dashboardService;

    @Autowired
    PostSellService postSellService;

    public void tick(Long startTime) {
        Long endTime = startTime + DURATION - 1;
        logger.info("Start time: " + new Date(startTime) + " end time: " + new Date(endTime));
        List<DashboardEntity> entities = postSellService.calculatePricePerM2(startTime, endTime);
        dashboardService.saveAll(entities);
        logger.info("Done calculate pricePerM2: " + entities.size());
    }

    @Override
    public void run(String... args) throws Exception {
        long now = System.currentTimeMillis();
        long lastTimeTrigger = dashboardService.getLastTimeTrigger();
        lastTimeTrigger = lastTimeTrigger > 0 ? lastTimeTrigger : (now - now % DURATION - 10 * DURATION - 1);
        while (true) {
            try {
                now = System.currentTimeMillis();
                if (lastTimeTrigger > now - DURATION) {
                    logger.info("Waiting for next tick ...");
                    Thread.sleep(DURATION);
                    continue;
                }
                tick(lastTimeTrigger + 1);
                lastTimeTrigger += DURATION;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
