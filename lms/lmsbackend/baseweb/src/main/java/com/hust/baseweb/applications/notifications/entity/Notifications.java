package com.hust.baseweb.applications.notifications.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "notifications")
@EntityListeners(AuditingEntityListener.class)
public class Notifications {

    public static final String STATUS_CREATED = "NOTIFICATION_CREATED";

    public static final String STATUS_READ = "NOTIFICATION_READ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String content;

    private String fromUser;

    private String toUser;

    private String url;

    private String statusId;

    @LastModifiedDate
    private Date lastUpdatedStamp;

    @CreatedDate
    private Date createdStamp;

}
