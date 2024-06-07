package com.real_estate.post.repositories;

import com.real_estate.post.models.postgresql.MessagePostgresEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<MessagePostgresEntity, Long> {

}
