package com.real_estate.post.daos.impls;

import com.real_estate.post.daos.interfaces.PostBuyDao;
import com.real_estate.post.dtos.response.CountPostByProvinceResponseDto;
import com.real_estate.post.dtos.response.PostBuyResponseDto;
import com.real_estate.post.models.AccountEntity;
import com.real_estate.post.models.PostBuyEntity;
import com.real_estate.post.models.postgresql.PostBuyPostgresEntity;
import com.real_estate.post.repositories.PostBuyRepository;
import com.real_estate.post.utils.PostStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("postBuyImpl")
public class PostBuyImpl implements PostBuyDao {
	@Autowired
	PostBuyRepository repository;

	@Autowired
	ModelMapper mapper;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public PostBuyEntity save(PostBuyEntity entity) {
		PostBuyPostgresEntity postgres = this.mapper.map(entity, PostBuyPostgresEntity.class);
		postgres = repository.saveAndFlush(postgres);
		return this.mapper.map(postgres, PostBuyEntity.class);
	}

	@Override
	public List<PostBuyEntity> findAll() {
		List<PostBuyPostgresEntity> postgresEntities = repository.findAll();
		return postgresEntities.stream().map(postEntity -> {
			return this.mapper.map(postEntity, PostBuyEntity.class);
		}).collect(Collectors.toList());
	}

	@Override
	public PostBuyEntity findById(Long postBuyId) {
		Optional<PostBuyPostgresEntity> postgresEntityOptional = repository.findById(postBuyId);
		if (postgresEntityOptional.isPresent()) {
			return this.mapper.map(postgresEntityOptional.get(), PostBuyEntity.class);
		} else {
			return null;
		}
	}

	@Override
	public List<PostBuyResponseDto> findPostBuyBy(Pageable pageable, String provinceId) {
		int limit = pageable.getPageSize();
		int offset = (int) pageable.getOffset();

		StringBuilder rawQuery = new StringBuilder();
		rawQuery.append(
				"select p, a " +
						"from PostBuyPostgresEntity p " +
						"left join AccountPostgresEntity a on p.authorId = a.accountId " );
		if (provinceId != null && provinceId != "") {
			rawQuery.append("where p.provinceId = '" + provinceId + "' ");
		}
		rawQuery.append("order by p.createdAt desc ");

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
	public Long countBy(String provinceId) {
		StringBuilder rawQuery = new StringBuilder();
		rawQuery.append(
				"select p, a " +
						"from PostBuyPostgresEntity p " +
						"left join AccountPostgresEntity a on p.authorId = a.accountId " +
						"where p.postStatus = 'OPENING' ");
		if (provinceId != null && provinceId != "") {
			rawQuery.append("and p.provinceId = '" + provinceId + "' ");
		}
		Query query = entityManager.createQuery(rawQuery.toString());
		List<Object[]> resultList = query.getResultList();
		return (long) resultList.size();
	}

	@Override
	public List<PostBuyResponseDto> findByAccountId(Long accountId) {
		Query query = entityManager.createQuery(
				"select p, a " +
						"from PostBuyPostgresEntity p " +
						"left join AccountPostgresEntity a " +
						"on p.authorId = a.accountId " +
						"where p.authorId = :accountId " +
						"order by p.createdAt desc");
		query.setParameter("accountId", accountId);
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
	public Integer updateStatusBy(Long postBuyId, Long accountId, PostStatus status) {
		return repository.updateStatusBy(postBuyId, accountId, status);
	}

	@Override
	public List<CountPostByProvinceResponseDto> countPost() {
		TypedQuery query = entityManager.createQuery(
				"select new com.real_estate.post.dtos.response.CountPostByProvinceResponseDto(" +
						"p.provinceId," +
						"p.nameProvince," +
						"count(p.provinceId)) " +
						"from PostBuyPostgresEntity p " +
						"group by p.provinceId, p.nameProvince " +
						"order by count(p.provinceId) desc",
				CountPostByProvinceResponseDto.class
		);

		return query.getResultList();
	}
}
