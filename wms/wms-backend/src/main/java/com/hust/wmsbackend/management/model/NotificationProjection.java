package com.hust.wmsbackend.management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hust.wmsbackend.management.entity.Notifications;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public interface NotificationProjection {
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

    default boolean getRead() {
        return getStatusId().equals(Notifications.STATUS_READ);
    }

    Date getCreatedStamp();

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
