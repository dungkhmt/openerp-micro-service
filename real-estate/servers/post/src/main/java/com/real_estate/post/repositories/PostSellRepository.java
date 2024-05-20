package com.real_estate.post.repositories;

import com.real_estate.post.models.postgresql.PostSellPostgresEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostSellRepository extends JpaRepository<PostSellPostgresEntity, Long> {
    @Query("select p from PostSellPostgresEntity p " +
            "where p.authorId = :accountId " +
            "order by p.createdAt desc ")
    public List<PostSellPostgresEntity> findByAccountId(long accountId);
}
