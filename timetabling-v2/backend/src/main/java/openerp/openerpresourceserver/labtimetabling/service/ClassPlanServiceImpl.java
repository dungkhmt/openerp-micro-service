package openerp.openerpresourceserver.labtimetabling.service;

import openerp.openerpresourceserver.labtimetabling.entity.ClassPlan;
import openerp.openerpresourceserver.labtimetabling.repo.ClassPlanRepo;

import java.util.List;

public class ClassPlanServiceImpl implements ClassPlanService{
    private ClassPlanRepo planningRepo;
    @Override
    public int batchInsert(List<ClassPlan> planningList) {
        long prev = planningRepo.count();
        planningRepo.saveAll(planningList);
        long curr = planningRepo.count();
        return (int) (curr-prev);
    }

    @Override
    public List<ClassPlan> getAllClasses() {
        return planningRepo.findAll();
    }

    @Override
    public List<ClassPlan> getClassesBySemester(String sem) {
        return planningRepo.findAllBySemester(sem);
    }

    @Override
    public List<String> getAllSemesters() {
        return planningRepo.findDistinctSemesters();
    }
}
