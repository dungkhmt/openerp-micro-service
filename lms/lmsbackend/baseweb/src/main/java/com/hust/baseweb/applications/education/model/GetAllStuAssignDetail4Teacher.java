package com.hust.baseweb.applications.education.model;

import java.util.ArrayList;
import java.util.List;

public class GetAllStuAssignDetail4Teacher {

    String studentId;
    String studentName;
    List<Assignment> assignmentList = new ArrayList<>();
    int totalSubmitedAssignment;

    private class Assignment {

        String assignmentId;
        String assignmentName;
        int assignmentStatus;

        public Assignment(GetAllStuAssigns4TeacherOM getAllStuAssigns4TeacherOM) {
            this.assignmentId = getAllStuAssigns4TeacherOM.getAssignmentId();
            this.assignmentName = getAllStuAssigns4TeacherOM.getAssignmentName();
            this.assignmentStatus = getAllStuAssigns4TeacherOM.getAssignmentSubmissionId() == null ? 0 : 1;
        }

        public String getAssignmentId() {
            return assignmentId;
        }

        public String getAssignmentName() {
            return assignmentName;
        }

        public int getAssignmentStatus() {
            return assignmentStatus;
        }
    }

    public GetAllStuAssignDetail4Teacher() {

    }

    public GetAllStuAssignDetail4Teacher(GetAllStuAssigns4TeacherOM getAllStuAssigns4TeacherOM) {
        this.studentId = getAllStuAssigns4TeacherOM.getId();
        this.studentName = getAllStuAssigns4TeacherOM.getName();
        Assignment assignment = new Assignment(getAllStuAssigns4TeacherOM);
        assignmentList.add(assignment);
        this.totalSubmitedAssignment = assignment.assignmentStatus;
    }

    public void addAssignment(GetAllStuAssigns4TeacherOM getAllStuAssigns4TeacherOM) {
        Assignment assignment = new Assignment(getAllStuAssigns4TeacherOM);
        this.assignmentList.add(assignment);
        this.totalSubmitedAssignment += assignment.assignmentStatus;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public List<Assignment> getAssignmentList() {
        return assignmentList;
    }

    public int getTotalSubmitedAssignment() {
        return totalSubmitedAssignment;
    }
}
