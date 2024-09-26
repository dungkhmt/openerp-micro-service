package com.hust.baseweb.applications.education.quiztest.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelInteractiveQuizAnswer {
    private UUID interactiveQuizId;
    private UUID questionId;
    private List<UUID> choiceAnswerId;
}