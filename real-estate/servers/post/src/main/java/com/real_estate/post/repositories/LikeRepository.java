package com.real_estate.post.repositories;

import com.real_estate.post.models.postgresql.LikePostgresEntity;
import com.real_estate.post.utils.TypePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<LikePostgresEntity, Long> {
    @Query("select l from LikePostgresEntity l where l.postId = :postId and l.likerId = :finderId and l.typePost = :typePost")
    List<LikePostgresEntity> findBy(Long postId, Long finderId, TypePost typePost);

    @Transactional
    @Modifying
    @Query("delete LikePostgresEntity l where l.likeId = :likeId and l.likerId = :likerId")
    void deleteBy(Long likeId, Long likerId);
}
