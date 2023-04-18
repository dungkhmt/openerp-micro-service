package com.hust.baseweb.applications.education.teacherclassassignment.dataprocessing;

import com.google.gson.Gson;
import com.hust.baseweb.applications.education.teacherclassassignment.model.AlgoClassIM;
import com.hust.baseweb.applications.education.teacherclassassignment.model.AlgoTeacherIM;
import com.hust.baseweb.applications.education.teacherclassassignment.model.Course4Teacher;
import com.hust.baseweb.applications.education.teacherclassassignment.model.ExtractDataOM;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        try {
            // Modify file path.
            TeacherExtracter teacherExtracter = new TeacherExtracter(new FileInputStream(new File(
                "D:/projects/baseweb/Data-phan-cong-giang-day-soict/course4teacher_20191.xlsx")));
            ClassExtracter classExtracter = new ClassExtracter(new FileInputStream(new File(
                "D:/projects/baseweb/Data-phan-cong-giang-day-soict/CNTT_20201.xlsx")));

            teacherExtracter.getIndexOfColumnIn("Sheet1");
            teacherExtracter.extract();

            classExtracter.getIndexOfColumnIn("Sheet1");
            classExtracter.extract();

            HashMap<AlgoClassIM, String> preAssignTeacher = classExtracter.getMClass2PreAssignedTeacher();

            // Write to file.
            ExtractDataOM extractDataOM = new ExtractDataOM();

            extractDataOM.setTeachers(teacherExtracter.getTeachers());
            extractDataOM.setClasses(classExtracter.getClasses());

            // add additional Course4Teacher based on solution plan
            List<AlgoTeacherIM> teacherIMList = teacherExtracter.getTeachers();
            HashMap<String, AlgoTeacherIM> mId2Teacher = new HashMap();
            for (AlgoTeacherIM t : teacherIMList) {
                mId2Teacher.put(t.getId(), t);
            }
            Set<AlgoClassIM> illegalClass = new HashSet();

            for (AlgoClassIM cls : classExtracter.getClasses()) {
                String teacherIds = classExtracter.getTeacherAssigned2Class(cls);
                if (teacherIds == null || teacherIds.equals("email@gmail.com")) {
                    illegalClass.add(cls);
                    continue;
                }
                List<String> listTeacherIds = new ArrayList<String>();
                if (teacherIds.contains("-")) {
                    String[] s = teacherIds.split("-");
                    for (String si : s) {
                        listTeacherIds.add(si.trim());
                    }
                } else {
                    listTeacherIds.add(teacherIds.trim());
                }
                for (String teacherId : listTeacherIds) {
                    if (teacherId != null) {
                        AlgoTeacherIM t = mId2Teacher.get(teacherId);
                        if (t == null) {
                            System.out.println("cannot find teacher of id " + teacherId);

                            t = new AlgoTeacherIM(teacherId, teacherId, new ArrayList<Course4Teacher>(), 0, true);
                            teacherIMList.add(t);
                            mId2Teacher.put(teacherId, t);

                            t.addIfNotExistCourse4Teacher(cls.getCourseId(), cls.getCourseName(), 1, cls.getClassType());

                        } else {
                            t.addIfNotExistCourse4Teacher(cls.getCourseId(), cls.getCourseName(), 1, cls.getClassType());
                        }
                    }
                }
            }
            List<AlgoClassIM> algoClassIMList = classExtracter.getClasses();
            for (AlgoClassIM cls : illegalClass) {
                algoClassIMList.remove((algoClassIMList.indexOf(cls)));
            }

            // Modify file path.
            BufferedWriter writer = new BufferedWriter(new FileWriter(
                "D:/projects/baseweb/Data-phan-cong-giang-day-soict/input_hour_ext_course4teacher.json"));

            Gson gson = new Gson();

            writer.write(gson.toJson(extractDataOM));

            writer.close();
            teacherExtracter.close();
            classExtracter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
