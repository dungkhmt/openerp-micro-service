package com.hust.baseweb.applications.programmingcontest.service.helper.garbagecollector;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Collector {

    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void manualCollectTrigger() {
        System.gc();
    }
}
