package com.real_estate.post.repositories;

import com.real_estate.post.models.postgresql.PostBuyPostgresEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PostBuyRepository extends JpaRepository<PostBuyPostgresEntity, Long> {
    @Query("select p from PostBuyPostgresEntity p " +
            "where p.authorId = :accountId " +
            "order by p.createdAt desc ")
    List<PostBuyPostgresEntity> findByAccountId(Long accountId);

    @Transactional
    @Modifying
    @Query("update PostBuyPostgresEntity p set p.postStatus = :status where p.postBuyId = :postBuyId and p.authorId = :accountId")
    public Integer updateStatusBy(Long postBuyId, Long accountId, String status);

}
