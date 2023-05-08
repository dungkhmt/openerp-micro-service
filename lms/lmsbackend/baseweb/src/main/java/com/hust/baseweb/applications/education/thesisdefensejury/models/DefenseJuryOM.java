package com.hust.baseweb.applications.education.thesisdefensejury.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DefenseJuryOM {
    private String name;
    private String program_name;
    private String userLoginID;
    private int maxThesis;
    private String thesisPlanName;
    private LocalDateTime defenseDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProgram_name() {
        return program_name;
    }

    public void setProgram_name(String program_name) {
        this.program_name = program_name;
    }

    public String getUserLoginID() {
        return userLoginID;
    }

    public void setUserLoginID(String userLoginID) {
        this.userLoginID = userLoginID;
    }

    public int getMaxThesis() {
        return maxThesis;
    }

    public void setMaxThesis(int maxThesis) {
        this.maxThesis = maxThesis;
    }

    public String getThesisPlanName() {
        return thesisPlanName;
    }

    public void setThesisPlanName(String thesisPlanName) {
        this.thesisPlanName = thesisPlanName;
    }

    public LocalDateTime getDefenseDate() {
        return defenseDate;
    }

    public void setDefenseDate(LocalDateTime defenseDate) {
        this.defenseDate = defenseDate;
    }
}
