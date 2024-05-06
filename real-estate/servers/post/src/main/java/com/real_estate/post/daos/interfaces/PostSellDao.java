package com.real_estate.post.daos.interfaces;



import com.real_estate.post.models.PostSellEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostSellDao {
    public PostSellEntity save(PostSellEntity entity);

    public List<PostSellEntity> findPostSellBy(
               Pageable pageable,
               String province,
               String district,
               Long minAcreage,
               Long fromPrice,
               Long toPrice,
               List<String> typeProperties,
               List<String> legalDocuments,
               List<String> directions,
               Long minFloor,
               Long minBathroom,
               Long minBedroom,
               Long minParking
    );

    public Long countBy(
            String province,
            String district,
            Long minAcreage,
            Long fromPrice,
            Long toPrice,
            List<String> typeProperties,
            List<String> legalDocuments,
            List<String> directions,
            Long minFloor,
            Long minBathroom,
            Long minBedroom,
            Long minParking
    );

    public PostSellEntity findById(Long postSellId);
}
