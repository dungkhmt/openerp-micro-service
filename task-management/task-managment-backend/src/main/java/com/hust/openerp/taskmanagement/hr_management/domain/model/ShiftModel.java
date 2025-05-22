package com.hust.openerp.taskmanagement.hr_management.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ShiftModel {
    private UUID id;
    private String userId;
    private LocalTime startTime;
    private LocalTime endTime;
    private String note;
    private LocalDate date;
    private Integer slots;

    public Integer getSlots(){
        return userId != null ? null : slots;
    }
}
