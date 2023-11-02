package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.entity.StudyWeek;

import java.util.List;

public interface StudyWeekService {

    List<StudyWeek> getStudyWeek();

    void updateStudyWeek();
}
