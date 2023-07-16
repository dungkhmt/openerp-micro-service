package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.WmsBackendApplication;
import com.hust.wmsbackend.management.entity.Notifications;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = WmsBackendApplication.class)
class NotificationsRepositoryTest {

    @Autowired
    NotificationsRepository notificationsRepository;

    @Test
    void getNotificationsByConditions() throws ParseException {
        List<Notifications> notificationsList = notificationsRepository.getNotificationsByUserIdAndStatusIdAndDateBeforeOrAt(
                "admin", "NOTIFICATION_CREATED", new SimpleDateFormat("dd/MM/yyyy").parse("24/06/2023"));
        System.out.printf("List size %s%n", notificationsList.size());
    }
}