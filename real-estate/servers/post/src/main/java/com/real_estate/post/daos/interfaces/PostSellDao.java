package com.real_estate.post.daos.interfaces;

import com.real_estate.post.dtos.response.PostSellResponseDto;
import com.real_estate.post.models.PostSellEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostSellDao {
    public PostSellEntity save(PostSellEntity entity);

    public List<PostSellResponseDto> findPostSellBy(
               Pageable pageable,
               String province,
               String district,
               Long fromAcreage,
               Long toAcreage,
               Long fromPrice,
               Long toPrice,
               List<String> typeProperties,
               List<String> directions
    );

    public Long countBy(
            String province,
            String district,
            Long fromAcreage,
            Long toAcreage,
            Long fromPrice,
            Long toPrice,
            List<String> typeProperties,
            List<String> directions
    );

    public PostSellEntity findById(Long postSellId);

    public List<PostSellEntity> findByAccountId(Long accountId);
}
