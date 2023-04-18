package com.hust.baseweb.applications.admin.dataadmin.repo;

import com.hust.baseweb.applications.notifications.entity.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface NotificationRepo extends JpaRepository<Notifications, UUID> {

    @Query(value="select * from notifications offset ?1 limit ?2", nativeQuery = true)
    List<Notifications> getPage(int offset, int limit);

    @Query(value="select count(*) from notifications", nativeQuery = true)
    int countNotifications();
}
