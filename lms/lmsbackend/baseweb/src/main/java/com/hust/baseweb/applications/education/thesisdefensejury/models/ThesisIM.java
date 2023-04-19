package com.hust.baseweb.applications.education.thesisdefensejury.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThesisIM {
    private UUID id;
    private String name;
    private String thesis_abstract;
    private String program_name;
    private String thesisPlanName;
    private String student_name;
    private String supervisor_name;
    private String reviewer_name;
    private String defense_jury_name;
    private String userLoginID;
    private List<String> keyword;
}
