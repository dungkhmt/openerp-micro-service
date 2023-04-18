package com.hust.baseweb.applications.education.thesisdefensejury.composite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
public class DefensePlanTeacherID implements Serializable {
    private String teacherId;
    private String thesisDefensePlanId;
}
