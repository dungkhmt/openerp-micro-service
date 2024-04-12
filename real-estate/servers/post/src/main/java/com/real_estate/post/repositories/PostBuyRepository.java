package com.real_estate.post.repositories;

import com.real_estate.common.models.postgres.PostBuyPostgresEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostBuyRepository extends JpaRepository<PostBuyPostgresEntity, Long> {
}
