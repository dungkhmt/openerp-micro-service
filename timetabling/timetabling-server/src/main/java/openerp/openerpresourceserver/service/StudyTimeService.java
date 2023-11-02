package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.entity.StudyTime;

import java.util.List;

public interface StudyTimeService {
    List<StudyTime> getStudyTime();

    void updateStudyTime();
}
