package com.hust.baseweb.applications.education.quiztest.entity.compositeid;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CompositeEduTestQuizGroupParticipationAssignmentId implements Serializable {

    private UUID quizGroupId;
    private String participationUserLoginId;

}
