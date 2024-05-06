package com.real_estate.post.daos.impls;

import com.real_estate.post.daos.interfaces.PostSellDao;
import com.real_estate.post.models.PostSellEntity;
import com.real_estate.post.models.postgresql.PostSellPostgresEntity;
import com.real_estate.post.repositories.PostSellRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public List<PostSellEntity> findPostSellBy(Pageable pageable,
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
    ) {

        int limit = pageable.getPageSize();
        int offset = (int) pageable.getOffset();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PostSellPostgresEntity> query = cb.createQuery(PostSellPostgresEntity.class);
        Root<PostSellPostgresEntity> post = query.from(PostSellPostgresEntity.class);
        List<Predicate> predicates = new ArrayList<>();

        if (province != null && province != "") {
            predicates.add(cb.equal(post.get("province"), province));
        }

        if (district != null && district != "") {
            predicates.add(cb.equal(post.get("district"), district));
        }

        predicates.add(cb.and(cb.between(post.get("price"), fromPrice, toPrice)));

        predicates.add(cb.greaterThanOrEqualTo(post.get("acreage"), minAcreage));
        predicates.add(cb.greaterThanOrEqualTo(post.get("floor"), minFloor));
        predicates.add(cb.greaterThanOrEqualTo(post.get("bathroom"), minBathroom));
        predicates.add(cb.greaterThanOrEqualTo(post.get("bedroom"), minBedroom));
        predicates.add(cb.greaterThanOrEqualTo(post.get("parking"), minParking));

        if (typeProperties != null && typeProperties.size() > 0) {
            predicates.add(post.get("typeProperty").in(typeProperties));
        }

        if (legalDocuments != null && legalDocuments.size() > 0) {
            predicates.add(post.get("legalDocuments").in(legalDocuments));
        }

        if (directions != null && directions.size() > 0) {
            predicates.add(post.get("directionsProperty").in(directions));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        Sort sort = pageable.getSort();
        if (sort != Sort.unsorted()) {
            List<Order> ordersSorted = new ArrayList<>();
            for (Sort.Order orderCondition : sort) {
                if (orderCondition.getDirection() == Sort.Direction.ASC) {
                    ordersSorted.add(cb.asc(post.get(orderCondition.getProperty())));
                } else {
                    ordersSorted.add(cb.desc(post.get(orderCondition.getProperty())));
                }
            }
            query.orderBy(ordersSorted);
        }

        List<PostSellPostgresEntity> result = entityManager.createQuery(query)
                .setFirstResult((int) offset)
                .setMaxResults(limit)
                .getResultList();


        return result.stream().map((postgresEntity) -> {
            return this.mapper.map(postgresEntity, PostSellEntity.class);
        }).collect(Collectors.toList());
    }

    @Override
    public Long countBy(String province,
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
    ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<PostSellPostgresEntity> post = query.from(PostSellPostgresEntity.class);
        List<Predicate> predicates = new ArrayList<>();

        Expression<Long> countId = cb.count(post.get("postSellId"));

        if (province != null && province != "") {
            predicates.add(cb.equal(post.get("province"), province));
        }

        if (district != null && district != "") {
            predicates.add(cb.equal(post.get("district"), district));
        }

        predicates.add(cb.and(cb.between(post.get("price"), fromPrice, toPrice)));

        predicates.add(cb.greaterThanOrEqualTo(post.get("acreage"), minAcreage));
        predicates.add(cb.greaterThanOrEqualTo(post.get("floor"), minFloor));
        predicates.add(cb.greaterThanOrEqualTo(post.get("bathroom"), minBathroom));
        predicates.add(cb.greaterThanOrEqualTo(post.get("bedroom"), minBedroom));
        predicates.add(cb.greaterThanOrEqualTo(post.get("parking"), minParking));

        if (typeProperties != null && typeProperties.size() > 0) {
            predicates.add(post.get("typeProperty").in(typeProperties));
        }

        if (legalDocuments != null && legalDocuments.size() > 0) {
            predicates.add(post.get("legalDocuments").in(legalDocuments));
        }

        if (directions != null && directions.size() > 0) {
            predicates.add(post.get("directionsProperty").in(directions));
        }

        query.select(countId).where(cb.and(predicates.toArray(new Predicate[0])));
        List<Long> result = entityManager.createQuery(query).getResultList();

        return result.isEmpty() || result.get(0) == null ? 0L : result.get(0);

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
}
