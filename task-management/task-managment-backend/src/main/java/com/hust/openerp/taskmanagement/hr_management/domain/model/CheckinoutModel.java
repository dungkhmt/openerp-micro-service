package com.hust.openerp.taskmanagement.hr_management.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.constant.CheckinoutType;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CheckinoutModel {
    private String userId;
    private LocalDateTime pointTime;
    private CheckinoutType type;
}
