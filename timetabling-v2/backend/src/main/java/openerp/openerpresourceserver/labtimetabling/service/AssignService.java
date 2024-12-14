package openerp.openerpresourceserver.labtimetabling.service;

import openerp.openerpresourceserver.labtimetabling.entity.Assign;
import openerp.openerpresourceserver.labtimetabling.entity.ScheduleConflict;

import java.util.List;

public interface AssignService {
    List<ScheduleConflict> findConflict();
    List<Assign> getAssignsBySemester(Long semId);
    List<String> getAllSemesters();
}
