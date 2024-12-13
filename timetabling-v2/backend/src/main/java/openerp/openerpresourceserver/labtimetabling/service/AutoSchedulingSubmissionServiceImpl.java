package openerp.openerpresourceserver.labtimetabling.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.labtimetabling.entity.autoscheduling.AutoSchedulingSubmission;
import openerp.openerpresourceserver.labtimetabling.repo.AutoSchedulingResultRepo;
import openerp.openerpresourceserver.labtimetabling.repo.AutoSchedulingSubmissionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class AutoSchedulingSubmissionServiceImpl implements AutoSchedulingSubmissionService{
    private AutoSchedulingSubmissionRepo repo;
    @Override
    public List<AutoSchedulingSubmission> getAllBySemesterId(Long semesterId) {
        return repo.findAllBySemester_id(semesterId);
    }

    @Override
    public List<AutoSchedulingSubmission> findAll() {
        return repo.findAll();
    }

    @Override
    public AutoSchedulingSubmission create(AutoSchedulingSubmission submission) {
        return repo.save(submission);
    }
}
