package com.hust.baseweb.applications.education.service;

import com.hust.baseweb.applications.education.report.model.quizparticipation.StudentQuizParticipationModel;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface LogUserLoginQuizQuestionService {

    Page<StudentQuizParticipationModel> findByClassId(UUID classId, Integer page, Integer size);

    Page<StudentQuizParticipationModel> getPageLogStudentQuiz(Integer page, Integer size);

    Page<StudentQuizParticipationModel> getPageLogStudentQuizOfAStudent(String userLoginId, Integer page, Integer size);
}
