package com.hust.baseweb.applications.education.quiztest.entity.compositeid;
import lombok.*;

import java.io.Serializable;
import java.util.*;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompositeInteractiveQuizAnswerId that = (CompositeInteractiveQuizAnswerId) o;
        return Objects.equals(interactiveQuizId, that.interactiveQuizId) &&
               Objects.equals(questionId, that.questionId) &&
               Objects.equals(userId, that.userId) &&
               Objects.equals(choiceAnswerId, that.choiceAnswerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interactiveQuizId, questionId, userId, choiceAnswerId);
    }

}
