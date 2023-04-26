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
public class DefenseJuryIM {
    private String name;
//    private String program_name;
    private String userLoginID;
    private int maxThesis;
    private String thesisPlanName;
    private LocalDateTime defenseDate;
}
