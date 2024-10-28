package com.hust.baseweb.applications.education.thesisdefensejury.repo;

import com.hust.baseweb.applications.education.thesisdefensejury.composite.DefensePlanTeacherID;
import com.hust.baseweb.applications.education.thesisdefensejury.entity.TeacherThesisDefensePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeacherThesisDefensePlanRepo extends JpaRepository<TeacherThesisDefensePlan, DefensePlanTeacherID> {

    @Query(value = "select * from teacher_thesis_defense_plan t where t.thesis_defense_plan_id = :defensePlanID",
           nativeQuery = true)
    List<TeacherThesisDefensePlan> findAllByDefensePlanID(String defensePlanID);

    @Query(value = "select * from teacher_thesis_defense_plan t where t.thesis_defense_plan_id = :defensePlanID and t.teacher_id =:teacherID",
           nativeQuery = true)
    TeacherThesisDefensePlan findByDefensePlanIDAndTeacherId(String defensePlanID, String teacherID);
}
