package openerp.openerpresourceserver.generaltimetabling.service;

import openerp.openerpresourceserver.generaltimetabling.model.entity.StudyWeek;

import java.util.List;

public interface StudyWeekService {

    List<StudyWeek> getStudyWeek();

    void updateStudyWeek();
}
