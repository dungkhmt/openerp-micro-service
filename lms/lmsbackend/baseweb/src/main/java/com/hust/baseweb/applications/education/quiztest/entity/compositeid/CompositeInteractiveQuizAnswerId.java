package com.hust.baseweb.applications.education.quiztest.entity.compositeid;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CompositeInteractiveQuizAnswerId implements Serializable {
    private UUID interactiveQuizId;
    private UUID questionId;
    private String userId;
    private UUID choiceAnswerId;
}
