package com.hust.baseweb.applications.education.quiztest.entity.compositeid;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CompositeEduQuizTestQuizQuestionId implements Serializable {
    private String testId;
    private UUID questionId;
}
