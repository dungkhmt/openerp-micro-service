package openerp.openerpresourceserver.labtimetabling.service;

import openerp.openerpresourceserver.labtimetabling.entity.ClassPlan;

import java.util.List;

public interface ClassPlanService {
    int batchInsert(List<ClassPlan> planningList);
    List<ClassPlan> getAllClasses();
    List<ClassPlan> getClassesBySemester(String sem);
    List<String> getAllSemesters();
}
