package com.hust.baseweb.applications.notifications.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@AllArgsConstructor
public class GetNotificationsOM {

    private Page notifications;

    private long numUnRead;
}
