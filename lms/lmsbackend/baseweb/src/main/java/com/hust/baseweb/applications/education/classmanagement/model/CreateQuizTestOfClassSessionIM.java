package com.hust.baseweb.applications.education.classmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateQuizTestOfClassSessionIM {
    private String testId;
    private String testName;
    private UUID sessionId;
    private int duration;

}
