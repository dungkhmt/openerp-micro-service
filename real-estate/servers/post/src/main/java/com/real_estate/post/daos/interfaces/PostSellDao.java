package com.real_estate.post.daos.interfaces;

import com.real_estate.post.dtos.response.PostSellResponseDto;
import com.real_estate.post.models.DashboardEntity;
import com.real_estate.post.models.PostBuyEntity;
import com.real_estate.post.models.PostSellEntity;
import com.real_estate.post.utils.PostStatus;
import com.real_estate.post.utils.TypeDirection;
import com.real_estate.post.utils.TypeProperty;
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
               List<TypeProperty> typeProperties,
               List<TypeDirection> directions
    );

    public Long countBy(
            String provinceId,
            String districtId,
            Long fromAcreage,
            Long toAcreage,
            Long fromPrice,
            Long toPrice,
            List<TypeProperty> typeProperties,
            List<TypeDirection> directions
    );

    public PostSellResponseDto findById(Long postSellId);

    public List<PostSellResponseDto> findByAccountId(Long accountId);

    public List<DashboardEntity> calculatePricePerM2(Long startTime, Long endTime);

    public Integer updateStatusBy(Long postSellId, Long accountId, PostStatus status);

    public List<PostSellResponseDto> findBy(PostBuyEntity buyEntity);
}
