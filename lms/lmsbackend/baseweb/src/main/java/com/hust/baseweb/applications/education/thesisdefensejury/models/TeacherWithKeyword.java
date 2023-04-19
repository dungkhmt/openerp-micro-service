package com.hust.baseweb.applications.education.thesisdefensejury.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeacherWithKeyword {
    private String teacherId;
    private String teacherName;
    private List<String> keywords;
}
