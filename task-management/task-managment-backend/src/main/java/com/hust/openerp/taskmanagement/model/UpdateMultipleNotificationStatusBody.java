package com.hust.openerp.taskmanagement.model;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.util.Date;

/**
 * @author Le Anh Tuan
 */
@Getter
@Setter
@Value
public class UpdateMultipleNotificationStatusBody {

    String status;

    Date beforeOrAt;
}
