package com.real_estate.post.daos.interfaces;


import com.real_estate.post.models.PostBuyEntity;

import java.util.List;

public interface PostBuyDao {
	public PostBuyEntity save(PostBuyEntity entity);

	public List<PostBuyEntity> findAll();
}
