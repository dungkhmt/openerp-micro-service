package openerp.openerpresourceserver.generaltimetabling.service;

import openerp.openerpresourceserver.generaltimetabling.model.entity.StudyTime;

import java.util.List;

public interface StudyTimeService {
    List<StudyTime> getStudyTime();

    void updateStudyTime();
}
