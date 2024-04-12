package com.real_estate.post.daos.impls;

import com.real_estate.common.models.PostBuyEntity;
import com.real_estate.common.models.postgres.PostBuyPostgresEntity;
import com.real_estate.post.daos.interfaces.PostBuyDao;
import com.real_estate.post.repositories.PostBuyRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("postBuyImpl")
public class PostBuyImpl implements PostBuyDao {
	@Autowired
	PostBuyRepository repo;

	@Autowired
	ModelMapper mapper;

	@Override
	public PostBuyEntity save(PostBuyEntity entity) {
		PostBuyPostgresEntity postgres = this.mapper.map(entity, PostBuyPostgresEntity.class);
		postgres = repo.saveAndFlush(postgres);
		return this.mapper.map(postgres, PostBuyEntity.class);
	}

	@Override
	public List<PostBuyEntity> findAll() {
		List<PostBuyPostgresEntity> postgresEntities = repo.findAll();
		return postgresEntities.stream().map(postEntity -> {
			return this.mapper.map(postEntity, PostBuyEntity.class);
		}).collect(Collectors.toList());
	}
}
