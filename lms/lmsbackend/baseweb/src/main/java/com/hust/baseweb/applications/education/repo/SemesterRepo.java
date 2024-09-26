package com.hust.baseweb.applications.education.repo;

import com.hust.baseweb.applications.education.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SemesterRepo extends JpaRepository<Semester, Integer> {

    Semester save(Semester semester);

    Semester findById(short semesterId);

    List<Semester> findAll();

    Semester findByActiveTrue();
}
