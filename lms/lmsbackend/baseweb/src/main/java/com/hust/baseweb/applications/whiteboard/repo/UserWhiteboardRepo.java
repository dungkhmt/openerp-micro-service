package com.hust.baseweb.applications.whiteboard.repo;

import com.hust.baseweb.applications.whiteboard.entity.UserWhiteboard;
import com.hust.baseweb.applications.whiteboard.entity.Whiteboard;
import com.hust.baseweb.entity.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserWhiteboardRepo
    extends JpaRepository<UserWhiteboard, UUID> {

    List<UserWhiteboard> findAllByUserLogin(UserLogin userLogin);
    UserWhiteboard findByWhiteboardIdAndUserLogin(String whiteboardId, UserLogin userLogin);
    List<UserWhiteboard> findAllByWhiteboard(Whiteboard whiteboard);
}
