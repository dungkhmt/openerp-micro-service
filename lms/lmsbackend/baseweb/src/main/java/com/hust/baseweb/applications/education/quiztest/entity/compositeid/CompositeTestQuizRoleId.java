package com.hust.baseweb.applications.education.quiztest.entity.compositeid;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CompositeTestQuizRoleId implements Serializable {

    private String testId;
    private String participantUserLoginId;
    private String roleId;
}
