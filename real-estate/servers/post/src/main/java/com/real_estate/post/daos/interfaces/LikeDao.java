package com.real_estate.post.daos.interfaces;

import com.real_estate.post.models.AccountEntity;
import com.real_estate.post.models.LikeEntity;
import com.real_estate.post.utils.TypePost;

import java.util.List;

public interface LikeDao {
    LikeEntity save(LikeEntity entity);

    void delete(Long likeId, Long likerId);

    Long getId(Long postId, Long finderId, TypePost typePost);

    List<Object> findAllLike(Long likerId);

    List<AccountEntity> findLiker(Long postId, TypePost typePost);
}
