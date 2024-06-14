package com.real_estate.post.repositories;

import com.real_estate.post.models.postgresql.SavePostPostgresEntity;
import com.real_estate.post.utils.TypePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SavePostRepository extends JpaRepository<SavePostPostgresEntity, Long> {
    @Query("select s from SavePostPostgresEntity s where s.postId = :postId and s.accountId = :accountId and s.typePost = :typePost")
    List<SavePostPostgresEntity> findBy(Long postId, Long accountId, TypePost typePost);

    @Transactional
    @Modifying
    @Query("delete SavePostPostgresEntity s where s.saveId = :saveId and s.accountId = :accountId")
    void deleteBy(Long saveId, Long accountId);
}
