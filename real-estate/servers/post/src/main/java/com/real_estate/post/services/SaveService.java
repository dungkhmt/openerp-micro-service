package com.real_estate.post.services;

import com.real_estate.post.daos.interfaces.SavePostDao;
import com.real_estate.post.dtos.response.AccountResponseDto;
import com.real_estate.post.models.SavePostEntity;
import com.real_estate.post.utils.TypePost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaveService {
    @Autowired
    @Qualifier("savePostImpl")
    SavePostDao savePostDao;

    public Long createSave(Long accountId, Long postId, TypePost typePost) {
        Long now = System.currentTimeMillis();
        SavePostEntity entity = new SavePostEntity().builder()
                .accountId(accountId)
                .postId(postId)
                .typePost(typePost)
                .createdAt(now)
                .build();
        entity = savePostDao.save(entity);
        return entity.getSaveId();
    }

    public void deleteSave(Long postId, Long accountId) {
        savePostDao.delete(postId, accountId);
    }

    public List<Object> getSaveBy(Long saverId) {
        return savePostDao.findPostBySaver(saverId);
    }

    public List<AccountResponseDto> getSaverOfPost(Long postId, TypePost typePost) {
        return savePostDao.findSaver(postId, typePost).stream().map(item -> {
            return new AccountResponseDto(item);
        }).toList();
    }
}
