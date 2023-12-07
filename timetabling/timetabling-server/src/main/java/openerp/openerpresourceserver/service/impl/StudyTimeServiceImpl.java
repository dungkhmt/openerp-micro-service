package openerp.openerpresourceserver.service.impl;

import openerp.openerpresourceserver.model.entity.StudyTime;
import openerp.openerpresourceserver.repo.StudyTimeRepo;
import openerp.openerpresourceserver.service.StudyTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudyTimeServiceImpl implements StudyTimeService {

    @Autowired
    private StudyTimeRepo studyTimeRepo;

    @Override
    public List<StudyTime> getStudyTime() {
        return studyTimeRepo.findAll();
    }

    @Override
    public void updateStudyTime() {
        List<String> studyTimeDataList = studyTimeRepo.getStudyTime();
        if (!studyTimeDataList.isEmpty()) {
            studyTimeRepo.deleteAll();
        }
        List<StudyTime> studyTimeList = new ArrayList<>();
        studyTimeDataList.forEach(el -> {
            String[] studyTimeData = el.split(",");
            StudyTime studyTime = StudyTime.builder()
                    .studyTime(studyTimeData[0])
                    .start(studyTimeData[1])
                    .finish(studyTimeData[2])
                    .crew(studyTimeData[0].equals("NULL") ? null : studyTimeData[3])
                    .build();
            studyTimeList.add(studyTime);
        });
        studyTimeRepo.saveAll(studyTimeList);
    }
}
