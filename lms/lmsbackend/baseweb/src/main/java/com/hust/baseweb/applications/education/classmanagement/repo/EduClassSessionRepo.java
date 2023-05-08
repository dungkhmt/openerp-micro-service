package com.hust.baseweb.applications.education.classmanagement.repo;

import com.hust.baseweb.applications.education.classmanagement.entity.EduClassSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EduClassSessionRepo extends JpaRepository<EduClassSession, UUID> {
    List<EduClassSession> findAllByClassId(UUID classId);

    EduClassSession findBySessionId(UUID sessionId);
}
