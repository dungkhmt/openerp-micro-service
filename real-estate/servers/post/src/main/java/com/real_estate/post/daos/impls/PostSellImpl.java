package com.real_estate.post.daos.impls;

import com.real_estate.post.daos.interfaces.PostSellDao;
import com.real_estate.post.dtos.response.PostSellResponseDto;
import com.real_estate.post.models.AccountEntity;
import com.real_estate.post.models.PostSellEntity;
import com.real_estate.post.models.postgresql.PostSellPostgresEntity;
import com.real_estate.post.repositories.PostSellRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("postSellImpl")
public class PostSellImpl implements PostSellDao {
    @Autowired
    PostSellRepository repository;

    @Autowired
    ModelMapper mapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public PostSellEntity save(PostSellEntity entity) {
        PostSellPostgresEntity postgres = this.mapper.map(entity, PostSellPostgresEntity.class);
        postgres = repository.saveAndFlush(postgres);
        return this.mapper.map(postgres, PostSellEntity.class);
    }

    @Override
    public List<PostSellResponseDto> findPostSellBy(Pageable pageable,
                                               String province,
                                               String district,
                                               Long fromAcreage,
                                               Long toAcreage,
                                               Long fromPrice,
                                               Long toPrice,
                                               List<String> typeProperties,
                                               List<String> directions
    ) {
        int limit = pageable.getPageSize();
        int offset = (int) pageable.getOffset();

        StringBuilder rawQuery = new StringBuilder();
        rawQuery.append(
                "select p, a " +
                "from PostSellPostgresEntity p " +
                "left join AccountPostgresEntity a on p.authorId = a.accountId " +
                "where p.postStatus = 'OPENING' ");
        if (province != null && province != "") {
            rawQuery.append("and p.province = '" + province + "' ");
        }
        if (district != null && district != "") {
            rawQuery.append("and p.district = '" + district + "' ");
        }

        rawQuery.append("and p.price >= " + fromPrice + " and p.price <= " + toPrice + " ");
        rawQuery.append("and p.acreage >= " + fromAcreage + " and p.acreage <= " + toAcreage + " ");
        rawQuery.append("and p.typeProperty in :typeProperties ");
        rawQuery.append("and p.directionsProperty in :directions ");
        rawQuery.append("order by p.createdAt desc ");

        Query query = entityManager.createQuery(rawQuery.toString());
        query.setParameter("typeProperties", typeProperties);
        query.setParameter("directions", directions);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        List<Object[]> resultList = query.getResultList();
        List<PostSellResponseDto> dtos = new ArrayList<>();
        for (Object[] row : resultList) {
            PostSellEntity post = this.mapper.map(row[0], PostSellEntity.class);
            AccountEntity account = this.mapper.map(row[1], AccountEntity.class);
            PostSellResponseDto combined = new PostSellResponseDto(post, account);
            dtos.add(combined);
        }
        return dtos;
    }

    @Override
    public Long countBy(
            String province,
            String district,
            Long fromAcreage,
            Long toAcreage,
            Long fromPrice,
            Long toPrice,
            List<String> typeProperties,
            List<String> directions
    ) {
        StringBuilder rawQuery = new StringBuilder();
        rawQuery.append(
                "select p, a " +
                        "from PostSellPostgresEntity p " +
                        "left join AccountPostgresEntity a on p.authorId = a.accountId " +
                        "where p.postStatus = 'OPENING' ");
        if (province != null && province != "") {
            rawQuery.append("and p.province = '" + province + "' ");
        }
        if (district != null && district != "") {
            rawQuery.append("and p.district = '" + district + "' ");
        }

        rawQuery.append("and p.price >= " + fromPrice + " and p.price <= " + toPrice + " ");
        rawQuery.append("and p.acreage >= " + fromAcreage + " and p.acreage <= " + toAcreage + " ");
        rawQuery.append("and p.typeProperty in :typeProperties ");
        rawQuery.append("and p.directionsProperty in :directions ");
        rawQuery.append("order by p.createdAt desc ");

        Query query = entityManager.createQuery(rawQuery.toString());
        query.setParameter("typeProperties", typeProperties);
        query.setParameter("directions", directions);
        List<Object[]> resultList = query.getResultList();
        return (long) resultList.size();
    }

    @Override
    public PostSellEntity findById(Long postSellId) {
        Optional<PostSellPostgresEntity> postgresEntityOptional = repository.findById(postSellId);
        if (postgresEntityOptional.isPresent()) {
            return this.mapper.map(postgresEntityOptional.get(), PostSellEntity.class);
        } else {
            return null;
        }
    }

    @Override
    public List<PostSellEntity> findByAccountId(Long accountId) {
        List<PostSellPostgresEntity> postgresEntities = repository.findByAccountId(accountId);
        return postgresEntities.stream().map((postgresEntity) -> {
            return this.mapper.map(postgresEntity, PostSellEntity.class);
        }).collect(Collectors.toList());
    }
}
