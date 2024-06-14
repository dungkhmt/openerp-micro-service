package com.real_estate.post.services;

import com.real_estate.post.daos.interfaces.LikeDao;
import com.real_estate.post.dtos.response.AccountResponseDto;
import com.real_estate.post.models.LikeEntity;
import com.real_estate.post.utils.TypePost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class LikeService {
    @Autowired
    @Qualifier("likeImpl")
    LikeDao likeDao;

    public Long createLike(Long likerId, Long postId, TypePost typePost) {
        Long existId = likeDao.getId(postId, likerId, typePost);
        if (existId > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bạn đã thích bài viết này trước đây");
        }
        Long now = System.currentTimeMillis();
        LikeEntity entity = new LikeEntity().builder()
                .likerId(likerId)
                .postId(postId)
                .typePost(typePost)
                .createdAt(now)
                .build();
        entity = likeDao.save(entity);
        return entity.getLikeId();
    }

    public void deleteLike(Long postId, Long likerId) {
        likeDao.delete(postId, likerId);
    }

    public List<Object> getLikeBy(Long saverId) {
        return likeDao.findAllLike(saverId);
    }

    public List<AccountResponseDto> getLiker(Long postId, TypePost typePost) {
        return likeDao.findLiker(postId, typePost).stream().map(item -> {
            return new AccountResponseDto(item);
        }).toList();
    }
}
