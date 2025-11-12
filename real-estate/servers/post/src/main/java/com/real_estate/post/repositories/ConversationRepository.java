package com.real_estate.post.repositories;

import com.real_estate.post.models.postgresql.ConversationPostgresEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<ConversationPostgresEntity, Long> {
}
