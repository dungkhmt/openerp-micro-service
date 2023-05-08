package com.hust.baseweb.applications.education.quiztest.model.quiztestgroupparticipant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddParticipantToQuizTestGroupOfAQuizTestInputModel {
    private String userLoginId;
    private String testId;
    private String groupCode;

}
