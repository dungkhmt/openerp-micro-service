package com.hust.baseweb.applications.education.teacherclassassignment.model;

import com.hust.baseweb.applications.education.teacherclassassignment.entity.ClassTeacherAssignmentClassInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

/**
 * Temporarily OK
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClassInfoForAssignment2TeacherModel {

    private UUID planId;

    private String classId;

    private String schoolName;

    private String semesterId;

    private String courseId;

    private String className;

    private String creditInfo;

    private String classNote;

    private String program;

    private String semesterType;

    private int enrollment;

    private int maxEnrollment;

    private String classType;

    private String timeTable;

    private String lesson;

    private String departmentId;

    private String createdByUserLoginId;

    private Date createdStamp;

    private double hourLoad;

    private int numberPossibleTeachers;

    private int numberPossibleTeachersInPlan;

    public ClassInfoForAssignment2TeacherModel(ClassTeacherAssignmentClassInfo c) {
        this.planId = c.getPlanId();
        this.classId = c.getClassId();
        this.className = c.getClassName();
        this.classNote = c.getNote();
        this.classType = c.getClassType();
        this.courseId = c.getCourseId();
        this.createdByUserLoginId = c.getCreatedBy();
        this.createdStamp = c.getCreatedStamp();
        this.creditInfo = c.getCredit();
        this.departmentId = c.getDepartmentId();
        this.enrollment = c.getEnrollment();
        this.hourLoad = c.getHourLoad();
        this.lesson = c.getLesson();
        this.timeTable = c.getTimetable();
        this.maxEnrollment = c.getMaxEnrollment();
        this.schoolName = c.getSchoolName();
        this.program = c.getProgram();
        this.semesterId = c.getSemesterId();
        this.semesterType = c.getSemesterType();
    }

}
