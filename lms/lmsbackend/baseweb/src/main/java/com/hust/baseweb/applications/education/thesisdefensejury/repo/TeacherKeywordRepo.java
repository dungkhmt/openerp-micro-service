package com.hust.baseweb.applications.education.thesisdefensejury.repo;

import com.hust.baseweb.applications.education.thesisdefensejury.entity.TeacherKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TeacherKeywordRepo extends JpaRepository<TeacherKeyword, String> {

    @Query(value = "select * from teacher_keyword tk where tk.teacher_id = :teacherId", nativeQuery = true)
    List<TeacherKeyword> findAllByTeacherId(String teacherId);

    @Query(value = "select * from teacher_keyword tk where tk.teacher_id = :teacherId and tk.keyword = :keyword",
           nativeQuery = true)
    TeacherKeyword findByTeacherIdAndKeyword(String teacherId, String keyword);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM teacher_keyword tk WHERE tk.teacher_id = :teacherId", nativeQuery = true)
    void deleteByTeacherID(String teacherId);


    @Modifying
    @Transactional
    @Query(value = "INSERT INTO teacher_keyword (teacher_id,keyword) values (:teacherId,:keyword)", nativeQuery = true)
    void insertByTeacherIdAndKeyword(String teacherId, String keyword);
}
