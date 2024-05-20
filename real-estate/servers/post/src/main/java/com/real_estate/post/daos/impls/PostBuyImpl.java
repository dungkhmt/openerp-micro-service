package com.real_estate.post.daos.impls;


import com.real_estate.post.daos.interfaces.PostBuyDao;
import com.real_estate.post.dtos.response.PostBuyResponseDto;
import com.real_estate.post.models.AccountEntity;
import com.real_estate.post.models.PostBuyEntity;
import com.real_estate.post.models.postgresql.PostBuyPostgresEntity;
import com.real_estate.post.repositories.PostBuyRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component("postBuyImpl")
public class PostBuyImpl implements PostBuyDao {
	@Autowired
	PostBuyRepository repo;

	@Autowired
	ModelMapper mapper;

	@PersistenceContext
	private EntityManager entityManager;

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

	@Override
	public List<PostBuyResponseDto> findPostBuyBy(Pageable pageable, String province, String district) {
		int limit = pageable.getPageSize();
		int offset = (int) pageable.getOffset();

		StringBuilder rawQuery = new StringBuilder();
		rawQuery.append(
				"select p, a " +
						"from PostBuyPostgresEntity p " +
						"left join AccountPostgresEntity a on p.authorId = a.accountId " +
						"where p.postStatus = 'OPENING' ");
		if (province != null && province != "") {
			rawQuery.append("and p.province = '" + province + "' ");
		}
		if (district != null && district != "") {
			rawQuery.append("and p.district = '" + district + "' ");
		}
		Query query = entityManager.createQuery(rawQuery.toString());
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		List<Object[]> resultList = query.getResultList();
		List<PostBuyResponseDto> dtos = new ArrayList<>();
		for (Object[] row : resultList) {
			PostBuyEntity post = this.mapper.map(row[0], PostBuyEntity.class);
			AccountEntity account = this.mapper.map(row[1], AccountEntity.class);
			PostBuyResponseDto combined = new PostBuyResponseDto(post, account);
			dtos.add(combined);
		}
		return dtos;
	}

	@Override
	public Long countBy(String province, String district) {
		StringBuilder rawQuery = new StringBuilder();
		rawQuery.append(
				"select p, a " +
						"from PostBuyPostgresEntity p " +
						"left join AccountPostgresEntity a on p.authorId = a.accountId " +
						"where p.postStatus = 'OPENING' ");
		if (province != null && province != "") {
			rawQuery.append("and p.province = '" + province + "' ");
		}
		if (district != null && district != "") {
			rawQuery.append("and p.district = '" + district + "' ");
		}
		Query query = entityManager.createQuery(rawQuery.toString());
		List<Object[]> resultList = query.getResultList();
		return (long) resultList.size();
	}
}
