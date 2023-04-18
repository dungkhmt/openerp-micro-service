package com.hust.baseweb.applications.education.thesisdefensejury.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThesisOM {
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
    private String keyword;
    private LocalDateTime updatedDateTime;
    private LocalDateTime createdTime;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThesis_abstract() {
        return thesis_abstract;
    }

    public void setThesis_abstract(String thesis_abstract) {
        this.thesis_abstract = thesis_abstract;
    }

    public String getProgram_name() {
        return program_name;
    }

    public void setProgram_name(String program_name) {
        this.program_name = program_name;
    }

    public String getThesisPlanName() {
        return thesisPlanName;
    }

    public void setThesisPlanName(String thesisPlanName) {
        this.thesisPlanName = thesisPlanName;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getSupervisor_name() {
        return supervisor_name;
    }

    public void setSupervisor_name(String supervisor_name) {
        this.supervisor_name = supervisor_name;
    }

    public String getReviewer_name() {
        return reviewer_name;
    }

    public void setReviewer_name(String reviewer_name) {
        this.reviewer_name = reviewer_name;
    }

    public String getDefense_jury_name() {
        return defense_jury_name;
    }

    public void setDefense_jury_name(String defense_jury_name) {
        this.defense_jury_name = defense_jury_name;
    }

    public String getUserLoginID() {
        return userLoginID;
    }

    public void setUserLoginID(String userLoginID) {
        this.userLoginID = userLoginID;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
