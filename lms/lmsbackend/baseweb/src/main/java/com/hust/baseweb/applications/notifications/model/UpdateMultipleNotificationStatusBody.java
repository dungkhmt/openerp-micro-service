package com.hust.baseweb.applications.notifications.model;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @author Le Anh Tuan
 */
@Getter
@Setter
@Value
public class UpdateMultipleNotificationStatusBody {

    @NotBlank
    String status;

    Date beforeOrAt;
}
