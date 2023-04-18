package com.hust.baseweb.applications.education.model.quiz;

import lombok.Getter;
import lombok.Setter;
import lombok.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelCreateQuizQuestionUserRole {
    private UUID questionId;
    private String userId;
    private String roleId;
}
