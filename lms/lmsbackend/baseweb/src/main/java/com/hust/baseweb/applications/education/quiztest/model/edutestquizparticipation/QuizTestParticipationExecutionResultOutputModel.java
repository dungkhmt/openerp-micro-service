package com.hust.baseweb.applications.education.quiztest.model.edutestquizparticipation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizTestParticipationExecutionResultOutputModel {

    private String testId;

    private UUID quizGroupId;

    private String quizGroupCode;

    private String participationUserLoginId;

    private String participationFullName;

    private UUID questionId;

    private String questionContent;

    List<QuizChoiceAnswerDTO> quizChoiceAnswerList;

    List<UUID> chooseAnsIds;

    private char result;// Y or N

    private int grade;// diem, ket qua

    private Date createdStamp;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class QuizChoiceAnswerDTO {

        private UUID choiceAnswerId;

        private String choiceAnswerContent;

        private char isCorrectAnswer;
    }

}


