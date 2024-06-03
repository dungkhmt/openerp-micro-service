package com.real_estate.post.daos.interfaces;


import com.real_estate.post.dtos.response.PostBuyResponseDto;
import com.real_estate.post.models.PostBuyEntity;
import com.real_estate.post.utils.PostStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostBuyDao {
	public PostBuyEntity save(PostBuyEntity entity);

	public List<PostBuyEntity> findAll();

	public PostBuyEntity findById(Long postBuyId);

	public List<PostBuyResponseDto> findPostBuyBy(
			Pageable pageable,
			String province,
			String district
	);

	public Long countBy(
			String province,
			String district
	);

	public List<PostBuyResponseDto> findByAccountId(Long accountId);

	public Integer updateStatusBy(Long postBuyId, Long accountId, PostStatus status);
}
