package com.hust.wmsbackend.management.model.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewNotificationRequest {
    private String toUser;
    private String content;
    private String url;
}
