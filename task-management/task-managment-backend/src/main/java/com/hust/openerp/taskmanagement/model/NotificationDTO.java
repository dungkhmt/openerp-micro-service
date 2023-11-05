package com.hust.openerp.taskmanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.hust.openerp.taskmanagement.entity.Notification.STATUS_READ;

/**
 * @author Le Anh Tuan
 */
public interface NotificationDTO {

    String getId();

    String getContent();

    @JsonIgnore
    String getFromUser();

    @JsonIgnore
    String getFirstName();

    @JsonIgnore
    String getMiddleName();

    @JsonIgnore
    String getLastName();

    @JsonIgnore
    String getStatusId();

    String getUrl();

    default boolean getRead() {
        return getStatusId().equals(STATUS_READ);
    }

    Date getCreatedStamp();

    default String getAvatar() {
        String firstName = getFirstName();
        String lastName = getLastName();

        String s1 = StringUtils.isBlank(firstName) ? "" : StringUtils.trim(firstName).substring(0, 1);
        String avatar = s1 + (StringUtils.isBlank(lastName) ? "" : StringUtils.trim(lastName).substring(0, 1));

        return avatar.equals("") ? null : avatar;
    }

    @JsonIgnore
    default String toJson() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id", getId());
        jsonObject.put("content", getContent());
        jsonObject.put("url", getUrl());
        jsonObject.put("avatar", getAvatar());
        jsonObject.put("read", getRead());
        jsonObject.put("createdStamp", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(getCreatedStamp()));

        return jsonObject.toJSONString();
    }
}
