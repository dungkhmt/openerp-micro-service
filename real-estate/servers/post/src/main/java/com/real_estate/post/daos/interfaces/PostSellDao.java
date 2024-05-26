package com.real_estate.post.daos.interfaces;

import com.real_estate.post.dtos.response.PostSellResponseDto;
import com.real_estate.post.models.DashboardPriceEntity;
import com.real_estate.post.models.PostSellEntity;
import com.real_estate.post.utils.PostStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostSellDao {
    public PostSellEntity save(PostSellEntity entity);

    public List<PostSellResponseDto> findPostSellBy(
               Pageable pageable,
               String provinceId,
               String districtId,
               Long fromAcreage,
               Long toAcreage,
               Long fromPrice,
               Long toPrice,
               List<String> typeProperties,
               List<String> directions
    );

    public Long countBy(
            String provinceId,
            String districtId,
            Long fromAcreage,
            Long toAcreage,
            Long fromPrice,
            Long toPrice,
            List<String> typeProperties,
            List<String> directions
    );

    public PostSellEntity findById(Long postSellId);

    public List<PostSellEntity> findByAccountId(Long accountId);

    public List<DashboardPriceEntity> calculatePricePerM2(Long startTime, Long endTime);

    public Integer updateStatusBy(Long postSellId, Long accountId, PostStatus status);
}
