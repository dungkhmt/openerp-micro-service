package com.hust.baseweb.applications.education.thesisdefensejury.repo;

import com.hust.baseweb.applications.education.thesisdefensejury.entity.DefenseJury;
import com.hust.baseweb.applications.education.thesisdefensejury.entity.TraningProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface TranningProgramRepo extends JpaRepository<TraningProgram, UUID> {

    @Override
    @Query(value = "select * from training_program where id= :Id", nativeQuery = true)
    Optional<TraningProgram> findById(UUID Id);

    Optional<TraningProgram> findByName(String name);
}
