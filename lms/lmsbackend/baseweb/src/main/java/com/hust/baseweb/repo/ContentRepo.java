package com.hust.baseweb.repo;

import com.hust.baseweb.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContentRepo extends JpaRepository<Content, UUID> {

    Content findByContentId(UUID id);
}
