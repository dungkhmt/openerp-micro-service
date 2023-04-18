package com.hust.baseweb.applications.education.model.quiz;

import com.hust.baseweb.applications.education.entity.QuizChoiceAnswer;
import com.hust.baseweb.applications.education.entity.QuizCourseTopic;
import com.hust.baseweb.applications.education.quiztest.model.QuizChoiceAnswerHideCorrectAnswer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class QuizQuestionDetailModel {

    private UUID questionId;

    private String statement;

    private QuizCourseTopic quizCourseTopic;

    private String levelId;

    private String statusId;

    private String createdStamp;

    //List<QuizChoiceAnswer> quizChoiceAnswerList;
    List<QuizChoiceAnswerHideCorrectAnswer> quizChoiceAnswerList;

    private List<byte[]> attachment;

    private String solutionContent;

    private String[] solutionAttachmentIds = {};

    private String createdByUserLoginId;

    private String questionContent;
}
