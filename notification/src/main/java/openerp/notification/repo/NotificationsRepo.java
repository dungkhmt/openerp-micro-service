package openerp.notification.repo;


import openerp.notification.entity.Notifications;
import openerp.notification.entity.QNotifications;
import openerp.notification.dto.NotificationDTO;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;

import java.util.UUID;

public interface NotificationsRepo
        extends JpaRepository<Notifications, UUID>,
        QuerydslPredicateExecutor<Notifications>,
        QuerydslBinderCustomizer<QNotifications> {

    long countByToUserAndStatusId(String toUser, String statusId);

    @Query(value = """
             select
            	cast(id as varchar),
            	content,
            	from_user fromUser,
            	url,
            	first_name firstName,
            	last_name lastName,
            	n.status_id statusId,
            	n.created_stamp createdStamp
            from
            	notifications n
            left join user_login ul on
            	n.from_user = ul.user_login_id
            where
            	n.to_user = ?
            """,
            nativeQuery = true)
    Page<NotificationDTO> findAllNotifications(String toUser, Pageable pageable);

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
            """,
            nativeQuery = true,
            countQuery = """
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
    Page<NotificationDTO> findNotificationsFromId(String toUser, UUID fromId, Pageable pageable);

    @Query(value = """
            select
            	cast(id as varchar),
            	content,
            	from_user fromUser,
            	url,
            	first_name firstName,
            	last_name lastName,
            	n.status_id statusId,
            	n.created_stamp createdStamp
            from
            	notifications n
            left join user_login ul on
            	n.from_user = ul.user_login_id
            where
            	n.id = ?1
            """,
            nativeQuery = true)
    NotificationDTO findNotificationById(UUID notificationId);

    @Override
    default void customize(
            QuerydslBindings bindings, QNotifications root
    ) {
        bindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);

        bindings.excluding(
                root.id,
                root.content,
                root.fromUser,
                root.toUser,
                root.lastUpdatedStamp,
                root.url,
                root.statusId
        );
    }
}
