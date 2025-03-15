package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.filter.impl;

import lombok.*;
import openerp.openerpresourceserver.constant.CheckinoutType;
import openerp.openerpresourceserver.application.port.out.checkinout.filter.ICheckinoutFilter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckinoutFilter implements ICheckinoutFilter {
    private String userId;
    private LocalDate date;
    private CheckinoutType type;
}
