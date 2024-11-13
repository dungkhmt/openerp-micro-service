package com.hust.baseweb.applications.education.teacherclassassignment.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Refactoring done
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimeTableStartAndDuration {

    // Mon = 2, Tue = 3,...
    private int day;

    private int startSlot;

    private int endSlot;

    private int duration;

    public String toString() {
        return startSlot + "-" + endSlot + "-" + duration;
    }
}
