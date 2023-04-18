package com.hust.baseweb.applications.education.service;

import com.hust.baseweb.applications.education.entity.EduCourse;
import com.hust.baseweb.applications.education.entity.QuizQuestion;
import com.hust.baseweb.applications.education.entity.QuizQuestionUserRole;
import com.hust.baseweb.applications.education.model.quiz.ModelCreateQuizQuestionUserRole;
import com.hust.baseweb.applications.education.model.quiz.QuizChooseAnswerInputModel;
import com.hust.baseweb.applications.education.model.quiz.QuizQuestionCreateInputModel;
import com.hust.baseweb.applications.education.model.quiz.QuizQuestionDetailModel;
import com.hust.baseweb.applications.education.model.quiz.QuizQuestionUpdateInputModel;
import com.hust.baseweb.entity.UserLogin;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface QuizQuestionService {

    QuizQuestion save(QuizQuestionCreateInputModel input);

    QuizQuestion save(UserLogin u, String json, MultipartFile[] files, MultipartFile[] solutionAttachments);

    List<QuizQuestion> findAll();

    List<QuizQuestion> findQuizOfCourse(String courseId);

    List<QuizQuestion> findQuizOfCourseTopic(String quizCourseTopicId);

    List<QuizQuestion> findAllQuizQuestionsByQuestionIdsIn(List<UUID> questionIds);

    EduCourse findCourseOfQuizQuestion(UUID questionId);

    QuizQuestionDetailModel findQuizDetail(UUID questionId);

    QuizQuestion changeOpenCloseStatus(UserLogin u, UUID questionId);

    boolean checkAnswer(String userId, QuizChooseAnswerInputModel quizChooseAnswerInputModel);

    QuizQuestionDetailModel findById(UUID questionId);

    QuizQuestion update(String userId, UUID questionId, String json, MultipartFile[] files, MultipartFile[] addedSolutionAttachments);

    QuizQuestionUserRole addQuizQuestionUserRole(ModelCreateQuizQuestionUserRole input);

    boolean grantRoleToUserOnAllQuizQuestions(String roleId, String userId);

    List<QuizQuestionUserRole> getUsersGranttedToQuizQuestion(UUID questionId);

    int generateChoiceCodesForAllQuizQuestions();
}
