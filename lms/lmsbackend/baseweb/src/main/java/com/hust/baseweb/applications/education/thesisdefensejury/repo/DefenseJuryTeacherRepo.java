package com.hust.baseweb.applications.education.thesisdefensejury.repo;

import com.hust.baseweb.applications.education.thesisdefensejury.composite.DefenseJuryTeacherID;
import com.hust.baseweb.applications.education.thesisdefensejury.entity.AcademicKeyword;
import com.hust.baseweb.applications.education.thesisdefensejury.entity.DefenseJuryTeacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface DefenseJuryTeacherRepo extends JpaRepository<DefenseJuryTeacher, DefenseJuryTeacherID> {

    @Query(value = "select * from defense_jury_teacher dt where dt.jury_id = :defenseJuryID", nativeQuery = true)
    List<DefenseJuryTeacher> findAllByDefenseJuryID(UUID defenseJuryID);
}
