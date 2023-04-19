package com.hust.baseweb.applications.education.report.service;

import com.hust.baseweb.applications.education.entity.LogUserLoginCourseChapterMaterial;
import com.hust.baseweb.applications.education.repo.LogUserLoginCourseChapterMaterialRepo;
import com.hust.baseweb.applications.education.report.model.GetClassParticipationStatisticInputModel;
import com.hust.baseweb.applications.education.report.model.StudentClassParticipationOutputModel;
import com.hust.baseweb.utils.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Log4j2
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class StudentClassParticipationServiceImpl implements StudentClassParticipationService {

    private LogUserLoginCourseChapterMaterialRepo logUserLoginCourseChapterMaterialRepo;


    @Override
    public List<StudentClassParticipationOutputModel> getStudentClassParticipationStatistic(
        GetClassParticipationStatisticInputModel input
    ) {
        /*
            TO BE IMPROVED
         */
        int len = 10;
        if (input.getLength() != 0) {
            len = input.getLength();
        }

        List<LogUserLoginCourseChapterMaterial> logUserLoginCourseChapterMaterials = logUserLoginCourseChapterMaterialRepo
            .findAll();

        List<StudentClassParticipationOutputModel> studentClassParticipationOutputModels = new ArrayList<>();
        /*
        studentClassParticipationOutputModels.add(new StudentClassParticipationOutputModel("2021-05-01",120));
        studentClassParticipationOutputModels.add(new StudentClassParticipationOutputModel("2021-05-02",150));
        studentClassParticipationOutputModels.add(new StudentClassParticipationOutputModel("2021-05-03",20));
        studentClassParticipationOutputModels.add(new StudentClassParticipationOutputModel("2021-05-04",60));
        studentClassParticipationOutputModels.add(new StudentClassParticipationOutputModel("2021-05-05",90));
        studentClassParticipationOutputModels.add(new StudentClassParticipationOutputModel("2021-05-06",120));
        */
        HashMap<String, Integer> mDate2Count = new HashMap();
        for (LogUserLoginCourseChapterMaterial i : logUserLoginCourseChapterMaterials) {
            Date date = i.getCreateStamp();
            if (date == null) {
                log.info("getStudentClassParticipationStatistic, date = NULL " + i.getEduCourseMaterialId());
                continue;
            }
            //log.info("getStudentClassParticipationStatistic, date = " + date.toString());
            String s_date = DateTimeUtils.date2YYYYMMDD(date);
            if (mDate2Count.get(s_date) == null) {
                mDate2Count.put(s_date, 1);
            } else {
                mDate2Count.put(s_date, mDate2Count.get(s_date) + 1);
            }
        }
        String[] s = new String[mDate2Count.keySet().size()];
        int idx = -1;
        for (String k : mDate2Count.keySet()) {
            idx++;
            s[idx] = k;
        }
        for (int i = 0; i < s.length; i++) {
            for (int j = i + 1; j < s.length; j++) {
                if (s[i].compareTo(s[j]) < 0) {
                    String tmp = s[i];
                    s[i] = s[j];
                    s[j] = tmp;
                }
            }
        }
        if (len > s.length) {
            len = s.length;
        }
        int acc =  0;
        for (int i = 0; i < len; i++) {
            //for(String sd: mDate2Count.keySet()){
            String sd = s[i];
            int count = mDate2Count.get(sd);
            acc = acc + count;
        }
        for (int i = 0; i < len; i++) {
            //for(String sd: mDate2Count.keySet()){
            String sd = s[i];
            int count = mDate2Count.get(sd);
            acc = acc - count;

            studentClassParticipationOutputModels.add(new StudentClassParticipationOutputModel(
                sd,
                count, acc));
            //log.info("getStudentClassParticipationStatistic, date " + sd + " -> " + mDate2Count.get(sd));
        }
        return studentClassParticipationOutputModels;
    }
}
