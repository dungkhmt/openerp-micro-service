package com.real_estate.post.repositories;

import com.real_estate.post.models.postgresql.PostSellPostgresEntity;
import com.real_estate.post.utils.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PostSellRepository extends JpaRepository<PostSellPostgresEntity, Long> {
    @Query("select p from PostSellPostgresEntity p " +
            "where p.authorId = :accountId " +
            "order by p.createdAt desc ")
    public List<PostSellPostgresEntity> findByAccountId(long accountId);

    @Transactional
    @Modifying
    @Query("update PostSellPostgresEntity p set p.postStatus = :status where p.postSellId = :postSellId and p.authorId = :accountId")
    public Integer updateStatusBy(Long postSellId, Long accountId, PostStatus status);
}
