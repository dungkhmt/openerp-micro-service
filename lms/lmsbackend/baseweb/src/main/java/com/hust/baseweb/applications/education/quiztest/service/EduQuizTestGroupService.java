package com.hust.baseweb.applications.education.quiztest.service;

import com.hust.baseweb.applications.education.quiztest.entity.EduTestQuizGroup;
import com.hust.baseweb.applications.education.quiztest.model.QuizGroupTestDetailModel;
import com.hust.baseweb.applications.education.quiztest.model.quiztestgroup.GenerateQuizTestGroupInputModel;

import java.security.Principal;
import java.util.List;

public interface EduQuizTestGroupService {

    List<EduTestQuizGroup> generateQuizTestGroups(GenerateQuizTestGroupInputModel input);

    QuizGroupTestDetailModel getTestGroupQuestionDetail(Principal principal, String testID);
    QuizGroupTestDetailModel getTestGroupQuestionDetailHeavyReload(Principal principal, String testID);
    QuizGroupTestDetailModel getTestGroupQuestionDetail(String userLoginId, String testID);


    QuizGroupTestDetailModel getTestGroupQuestionDetailOfGroupCode(String userLoginId, String groupCode, String testId);

    List<QuizGroupTestDetailModel> getQuizTestGroupWithQuestionsDetail(String testId);
    QuizGroupTestDetailModel getTestGroupQuestionDetailNotUsePermutationConfig(String userLoginId, String testID);
    QuizGroupTestDetailModel getTestGroupQuestionDetailNotUsePermutationConfigHeavyReload(String userLoginId, String testID);
    QuizGroupTestDetailModel getQuestionsDetailOfQuizGroup(String groupCode, String testID);

    QuizGroupTestDetailModel getQuestionsDetailWithUserExecutionChoideOfQuizGroupNotUsePermutationConfig(String userLoginId, String groupCode, String testID);

    EduTestQuizGroup getQuizTestGroupFrom(String groupCode, String testId);
}
