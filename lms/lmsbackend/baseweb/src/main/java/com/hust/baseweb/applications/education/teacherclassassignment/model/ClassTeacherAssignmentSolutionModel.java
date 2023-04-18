package com.hust.baseweb.applications.education.teacherclassassignment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * OK
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClassTeacherAssignmentSolutionModel {

    private UUID solutionItemId;

    private String classCode;

    private String classType;

    private String courseId;

    private String courseName;

    private String teacherId;

    private String teacherName;

    private String timetable;

//    private String timetableCode;

    private double hourLoad;

    private boolean pinned;

    // Data structures for viewing under grid
    private int startSlot;

    private int endSlot;

    private int startIndexFromPrevious; // so tiet trong ke tu tiet cuoi cung cua lop truoc

    private int duration; // so tiet

    /**
     * OK
     *
     * @return
     */
    private ClassTeacherAssignmentSolutionModel duplicate() {
        ClassTeacherAssignmentSolutionModel model = new ClassTeacherAssignmentSolutionModel();

        model.setSolutionItemId(this.getSolutionItemId());
        model.setClassCode(this.getClassCode());
        model.setClassType(this.getClassType());
        model.setCourseId(this.getCourseId());
        model.setCourseName(this.getCourseName());
        model.setTeacherId(this.getTeacherId());
        model.setTeacherName(this.getTeacherName());
        model.setTimetable(this.getTimetable());
//        model.setTimetableCode(this.getTimetableCode());
        model.setHourLoad(this.getHourLoad());
        model.setPinned(this.isPinned());
        model.setEndSlot(this.getEndSlot());
        model.setStartSlot(this.getStartSlot());
        model.setDuration(this.getDuration());
        model.setStartIndexFromPrevious(this.getStartIndexFromPrevious());

        return model;
    }

    /**
     * OK
     *
     * @return
     */
    public boolean isMultipleFragments() {
        String[] s = timetable.split(";");
        if (s.length == 0) {
            return false;
        }

        int cnt = 0;
        for (String value : s) {
            if (value.trim().length() > 3) {
                cnt++;
            }
        }

        return cnt > 1;
    }

    /**
     * OK
     *
     * @return
     */
    public ClassTeacherAssignmentSolutionModel[] checkMultipleFragmentsAndDuplicate() {
        String[] s = timetable.split(";");
        if (s.length == 0) {
            return null;
        }

        int cnt = 0;
        for (String value : s) {
            if (value.trim().length() > 3) {
                cnt++;
            }
        }

        ClassTeacherAssignmentSolutionModel[] models = new ClassTeacherAssignmentSolutionModel[cnt];
        for (int i = 0; i < models.length; i++) {
            models[i] = this.duplicate();
            models[i].setTimetable(s[i]);
        }

        return models;
    }
}
