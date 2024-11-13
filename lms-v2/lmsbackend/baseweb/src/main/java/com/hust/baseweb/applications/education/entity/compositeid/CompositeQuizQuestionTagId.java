package com.hust.baseweb.applications.education.entity.compositeid;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CompositeQuizQuestionTagId implements Serializable {

    private UUID questionId;
    private UUID tagId;
}
