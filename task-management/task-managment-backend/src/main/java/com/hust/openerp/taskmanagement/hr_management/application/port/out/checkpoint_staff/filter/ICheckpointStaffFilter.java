package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_staff.filter;


import java.util.List;
import java.util.UUID;

public interface ICheckpointStaffFilter {
    String getUserId();
    List<String> getUserIds();
    UUID getPeriodId();
    //List<String> getConfigureIds();
}
