package com.real_estate.post.repositories;

import com.real_estate.post.models.postgresql.PostSellPostgresEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostSellRepository extends JpaRepository<PostSellPostgresEntity, Long> {
}
