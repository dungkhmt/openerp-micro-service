package com.hust.baseweb.applications.education.quiztest.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InteractiveQuizInputModel {
    private String interactiveQuizName;
    private UUID sessionId;
    private String statusId;
    
}
