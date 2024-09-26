package com.hust.baseweb.applications.education.teacherclassassignment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlgoTeacherIM {

    private String id;

    private String name;

    // Danh sach cac mon hoc ma giao vien co the day trong dot phan cong
    private List<Course4Teacher> courses;

    // So gio da duoc phan cong boi nhiem vu giang day khac
    private double prespecifiedHourLoad;

    private boolean minimizeNumberWorkingDays;

    public void addIfNotExistCourse4Teacher(String courseId, String courseName, int priority, String type) {
        for (Course4Teacher ct : courses) {
            if (ct.getId().equals(courseId) && ct.getClassType().equals(type)) {
                return;
            }
        }
        courses.add(new Course4Teacher(courseId, courseName, priority, type));
    }
}
