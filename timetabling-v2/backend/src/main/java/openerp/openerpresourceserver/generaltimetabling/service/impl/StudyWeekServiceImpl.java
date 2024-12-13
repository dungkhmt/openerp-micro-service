package openerp.openerpresourceserver.generaltimetabling.service.impl;

import openerp.openerpresourceserver.generaltimetabling.model.entity.StudyWeek;
import openerp.openerpresourceserver.generaltimetabling.repo.StudyWeekRepo;
import openerp.openerpresourceserver.generaltimetabling.service.StudyWeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudyWeekServiceImpl implements StudyWeekService {

    @Autowired
    private StudyWeekRepo studyWeekRepo;

    @Override
    public List<StudyWeek> getStudyWeek() {
        return studyWeekRepo.findAll();
    }

    @Override
    public void updateStudyWeek() {
        List<String> studyWeekDataList = studyWeekRepo.getStudyWeek();
        if (!studyWeekDataList.isEmpty()) {
            studyWeekRepo.deleteAll();
        }
        List<StudyWeek> studyWeekList = new ArrayList<>();
        studyWeekDataList.forEach(el -> {
            StudyWeek studyWeek = StudyWeek.builder()
                    .studyweek(el)
                    .build();
            studyWeekList.add(studyWeek);
        });
        studyWeekRepo.saveAll(studyWeekList);
    }

}
