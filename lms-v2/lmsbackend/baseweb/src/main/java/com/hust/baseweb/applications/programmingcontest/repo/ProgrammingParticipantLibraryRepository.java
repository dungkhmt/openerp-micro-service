package com.hust.baseweb.applications.programmingcontest.repo;

import com.hust.baseweb.applications.programmingcontest.entity.ProgrammingParticipantLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProgrammingParticipantLibraryRepository extends CrudRepository<ProgrammingParticipantLibrary, UUID> {
    List<ProgrammingParticipantLibrary> findByUserId(String userId);
}
