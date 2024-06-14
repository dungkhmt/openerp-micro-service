package com.real_estate.post.daos.interfaces;

import com.real_estate.post.models.AccountEntity;
import com.real_estate.post.models.SavePostEntity;
import com.real_estate.post.utils.TypePost;

import java.util.List;

public interface SavePostDao {
    SavePostEntity save(SavePostEntity entity);

    void delete(Long saveId, Long accountId);

    Long getId(Long postId, Long accountId, TypePost typePost);

    List<Object> findPostBySaver(Long accountId);

    List<AccountEntity> findSaver(Long postId, TypePost typePost);
}
