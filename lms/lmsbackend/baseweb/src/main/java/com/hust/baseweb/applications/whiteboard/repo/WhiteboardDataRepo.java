package com.hust.baseweb.applications.whiteboard.repo;

import com.hust.baseweb.applications.whiteboard.entity.WhiteBoardData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WhiteboardDataRepo extends JpaRepository<WhiteBoardData, String> {
    WhiteBoardData findWhiteBoardDataByWhiteboardId(String whiteboardId);
}
