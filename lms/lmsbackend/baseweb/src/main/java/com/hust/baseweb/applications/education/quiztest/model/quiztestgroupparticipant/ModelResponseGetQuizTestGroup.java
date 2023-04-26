package com.hust.baseweb.applications.education.quiztest.model.quiztestgroupparticipant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ModelResponseGetQuizTestGroup {
    private String groupCode;
    private UUID groupId;
    private String statusId;
}
