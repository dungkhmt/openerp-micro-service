package com.hust.baseweb.applications.education.quiztest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelCreateQuizTestsOfClassSessionInput {
    private UUID sessionId;
    //private String sessionSequenceIndex;
    private int numberTests;
    private int duration;
}
