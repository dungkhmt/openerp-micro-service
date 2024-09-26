package com.hust.baseweb.applications.education.quiztest.model.quiztestgroup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizTestGroupParticipantAssignmentOutputModel {

    private UUID quizTestGroupId;
    private String quizTestGroupCode;
    private String participantUserLoginId;
    private String fullName;
}
