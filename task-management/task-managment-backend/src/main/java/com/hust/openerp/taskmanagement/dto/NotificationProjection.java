package com.hust.openerp.taskmanagement.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hust.openerp.taskmanagement.entity.Notification;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Optional;

public interface NotificationProjection {

    // DateFormat ISO_8601_DATE_FORMAT = new
    // SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    String getId();

    String getContent();

    @JsonIgnore
    String getFromUser();

    @JsonIgnore
    String getFirstName();

    @JsonIgnore
    String getLastName();

    @JsonIgnore
    String getStatusId();

    String getUrl();

    Date getCreatedStamp();

    default boolean getRead() {
        return Optional.of(getStatusId()).map(status -> status.equals(Notification.STATUS_READ)).orElseGet(() -> false);
    }

    default String getAvatar() {
        String firstName = getFirstName();
        String lastName = getLastName();

        String s1 = StringUtils.isBlank(firstName) ? "" : StringUtils.trim(firstName).substring(0, 1);
        String avatar = s1 + (StringUtils.isBlank(lastName) ? "" : StringUtils.trim(lastName).substring(0, 1));

        return avatar.equals("") ? null : avatar;
    }

    default NotificationDTO toDTO() {
        return new NotificationDTO(getId(), getContent(), getUrl(), getAvatar(), getRead(), getCreatedStamp());
    }
}
