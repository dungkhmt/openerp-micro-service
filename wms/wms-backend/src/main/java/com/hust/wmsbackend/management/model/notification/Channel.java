package com.hust.wmsbackend.management.model.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Channel {
    private InAppChannel inApp;
    private EmailChannel email;
}
