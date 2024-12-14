package openerp.openerpresourceserver.labtimetabling.service;

import openerp.openerpresourceserver.labtimetabling.entity.autoscheduling.AutoSchedulingSubmission;

import java.util.List;

public interface AutoSchedulingSubmissionService {
    List<AutoSchedulingSubmission> getAllBySemesterId(Long semesterId);
    List<AutoSchedulingSubmission> findAll();

    AutoSchedulingSubmission create(AutoSchedulingSubmission submission);

}
