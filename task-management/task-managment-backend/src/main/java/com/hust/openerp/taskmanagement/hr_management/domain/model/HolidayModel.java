package com.hust.openerp.taskmanagement.hr_management.domain.model;

import com.hust.openerp.taskmanagement.hr_management.constant.HolidayType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class HolidayModel {
    private UUID id;
    private String name;
    private HolidayType type;
    private List<LocalDate> dates;
}
