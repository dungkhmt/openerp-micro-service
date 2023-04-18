package com.hust.baseweb.applications.whiteboard.repo;

import com.hust.baseweb.applications.education.classmanagement.entity.EduClassSession;
import com.hust.baseweb.applications.whiteboard.entity.Whiteboard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WhiteboardRepo extends JpaRepository<Whiteboard, String> {
    List<Whiteboard> findAll();

    Whiteboard findWhiteboardById(String whiteboardId);

    List<Whiteboard> findAllByEduClassSession(EduClassSession eduClassSession);
}
