package com.hust.openerp.taskmanagement.repository;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hust.openerp.taskmanagement.dto.NotificationProjection;
import com.hust.openerp.taskmanagement.entity.Notification;

public interface NotificationRepository
        extends JpaRepository<Notification, UUID> {

    long countByToUserAndStatusId(String toUser, String statusId);

    @Query(value = """
                select
                    n.id id,
                    n.content content,
                    n.fromUser fromUser,
                    u.firstName firstName,
                    u.lastName lastName,
                    n.statusId statusId,
                    n.url url,
                    n.createdStamp createdStamp
                from Notification n
                left join User u on n.fromUser = u.id
                where n.toUser = :toUser
            """)
    Page<NotificationProjection> findAllNotifications(String toUser, Pageable pageable);

    @Query(value = """
                with cte as (
                select
                    created_stamp
                from
                    notifications
                where
                    id = ?2 )
                select
                    cast(id as varchar),
                    content,
                    from_user fromUser,
                    url,
                    first_name firstName,
                    last_name lastName,
                    n2.status_id statusId,
                    n2.created_stamp createdStamp
                from
                    notifications n2
                left join user_login ul on
                    n2.from_user = ul.user_login_id,
                    cte
                where
                    n2.to_user = ?1
                    and n2.created_stamp < cte.created_stamp
            """, nativeQuery = true, countQuery = """
                with cte as (
                select
                    created_stamp
                from
                    notifications
                where
                    id = ?2 )
                select
                    count(n2.id)
                from
                    notifications n2
                left join user_login ul on
                    n2.from_user = ul.user_login_id,
                    cte
                where
                    n2.to_user = ?1
                    and n2.created_stamp < cte.created_stamp
            """)
    Page<NotificationProjection> findNotificationsFromId(String toUser, UUID fromId, Pageable pageable);

    @Query(value = """
                select
                    n.id id,
                    n.content content,
                    n.fromUser fromUser,
                    u.firstName firstName,
                    u.lastName lastName,
                    n.statusId statusId,
                    n.url url,
                    n.createdStamp createdStamp
                from
                    Notification n
                left join User u on
                    n.fromUser = u.id
                where
                    n.id = ?1
            """)
    NotificationProjection findNotificationById(UUID notificationId);

    @Query(value = """
            select n from Notification n
            where n.toUser = ?1 and n.statusId = 'NOTIFICATION_CREATED' and n.createdStamp <= ?2
            """)
    Iterable<Notification> findUnreadAndBeforeOrAtTime(String userId, Date time);
}
