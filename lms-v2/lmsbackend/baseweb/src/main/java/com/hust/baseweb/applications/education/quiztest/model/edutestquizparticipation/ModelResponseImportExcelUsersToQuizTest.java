package com.hust.baseweb.applications.education.quiztest.model.edutestquizparticipation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelResponseImportExcelUsersToQuizTest {
    private String userId;
    private String fullName;
    private String refUserId;
    private String email;
    private String code;
}
