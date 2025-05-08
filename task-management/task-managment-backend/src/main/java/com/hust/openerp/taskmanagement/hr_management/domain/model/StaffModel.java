package com.hust.openerp.taskmanagement.hr_management.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.constant.StaffStatus;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
public class StaffModel {
    protected String staffCode;
    protected String userLoginId;
    protected String fullname;
    protected StaffStatus status;
    protected String email;
    protected LocalDate dateOfJoin;
    protected Float leaveHours;
}
