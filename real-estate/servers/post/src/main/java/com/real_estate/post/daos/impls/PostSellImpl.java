package com.real_estate.post.daos.impls;

import com.real_estate.post.daos.interfaces.PostSellDao;
import com.real_estate.post.dtos.response.PostSellResponseDto;
import com.real_estate.post.models.AccountEntity;
import com.real_estate.post.models.DashboardPriceEntity;
import com.real_estate.post.models.PostBuyEntity;
import com.real_estate.post.models.PostSellEntity;
import com.real_estate.post.models.postgresql.PostSellPostgresEntity;
import com.real_estate.post.repositories.PostSellRepository;
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
    ) {
        int limit = pageable.getPageSize();
        int offset = (int) pageable.getOffset();

        StringBuilder rawQuery = new StringBuilder();
        rawQuery.append(
                "select p, a " +
                "from PostSellPostgresEntity p " +
                "left join AccountPostgresEntity a on p.authorId = a.accountId " +
                "where p.postStatus = 'OPENING' ");
        if (provinceId != null && provinceId != "") {
            rawQuery.append("and p.provinceId = '" + provinceId + "' ");
        }
        if (districtId != null && districtId != "") {
            rawQuery.append("and p.districtId = '" + districtId + "' ");
        }

        if (fromPrice != null) {
            rawQuery.append("and p.price >= " + fromPrice + " ");
        }
        if (toPrice != null) {
            rawQuery.append("and p.price <= " + toPrice + " ");
        }
        if (fromAcreage != null) {
            rawQuery.append("and p.acreage >= " + fromAcreage + " ");
        }        if (toAcreage != null) {
            rawQuery.append("and p.acreage <= " + toAcreage + " ");
        }

        rawQuery.append("and p.typeProperty in :typeProperties ");
        rawQuery.append("and p.directionProperty in :directions ");
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
            String provinceId,
            String districtId,
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
        if (provinceId != null && provinceId != "") {
            rawQuery.append("and p.provinceId = '" + provinceId + "' ");
        }
        if (districtId != null && districtId != "") {
            rawQuery.append("and p.districtId = '" + districtId + "' ");
        }

        if (fromPrice != null) {
            rawQuery.append("and p.price >= " + fromPrice + " ");
        }
        if (toPrice != null) {
            rawQuery.append("and p.price <= " + toPrice + " ");
        }
        if (fromAcreage != null) {
            rawQuery.append("and p.acreage >= " + fromAcreage + " ");
        }        if (toAcreage != null) {
            rawQuery.append("and p.acreage <= " + toAcreage + " ");
        }
        rawQuery.append("and p.typeProperty in :typeProperties ");
        rawQuery.append("and p.directionProperty in :directions ");
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
    public List<PostSellResponseDto> findByAccountId(Long accountId) {
        Query query = entityManager.createQuery(
                "select p, a " +
                        "from PostSellPostgresEntity p " +
                        "left join AccountPostgresEntity a " +
                        "on p.authorId = a.accountId " +
                        "where p.authorId = :accountId " +
                        "order by p.createdAt desc");
        query.setParameter("accountId", accountId);
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
    public List<DashboardPriceEntity> calculatePricePerM2(Long startTime, Long endTime) {
        TypedQuery<DashboardPriceEntity> query = entityManager.createQuery(
                "select new com.real_estate.post.models.DashboardPriceEntity(" +
                        "p.districtId," +
                        "p.nameDistrict," +
                        "p.typeProperty," +
                        "max (p.pricePerM2)," +
                        "min (p.pricePerM2)," +
                        "avg (p.pricePerM2)," +
                        "count(p.districtId)," +
                        ":startTime," +
                        ":endTime" +
                        ") " +
                        "from PostSellPostgresEntity p " +
                        "where p.createdAt > :startTime and p.createdAt < :endTime " +
                        "group by p.districtId, p.nameDistrict, p.typeProperty"
                , DashboardPriceEntity.class);
        query.setParameter("startTime", startTime);
        query.setParameter("endTime", endTime);
        List<DashboardPriceEntity> entities = query.getResultList();
        return entities;
    }

    @Override
    public Integer updateStatusBy(Long postSellId, Long accountId, PostStatus status) {
        return repository.updateStatusBy(postSellId, accountId, status.toString());
    }

    @Override
    public List<PostSellResponseDto> findBy(PostBuyEntity buyEntity) {
        StringBuilder rawQuery = new StringBuilder();
        rawQuery.append(
                "select p, a " +
                        "from PostSellPostgresEntity p " +
                        "left join AccountPostgresEntity a on p.authorId = a.accountId " +
                        "where p.postStatus = 'OPENING' ");
        rawQuery.append("and p.provinceId = '" + buyEntity.getProvinceId() + "' ");
        rawQuery.append("and p.districtId in :districtIds ");
        rawQuery.append("and p.typeProperty = '" + buyEntity.getTypeProperty() + "' ");
        rawQuery.append("and p.authorId != " + buyEntity.getAuthorId() + " ");
        rawQuery.append("and p.price >= " + buyEntity.getMinPrice() + " ");
        if (buyEntity.getMaxPrice() != 0) {
            rawQuery.append("and p.price <= " + buyEntity.getMaxPrice() + " ");
        }
        rawQuery.append("and p.acreage >= " + buyEntity.getMinAcreage() + " ");
        if (buyEntity.getMaxAcreage() != 0) {
            rawQuery.append("and p.acreage <= " + buyEntity.getMaxAcreage() + " ");
        }

        rawQuery.append("and p.bathroom >= " + buyEntity.getMinBathroom() + " ");
        rawQuery.append("and p.parking >= " + buyEntity.getMinParking() + " ");
        rawQuery.append("and p.bedroom >= " + buyEntity.getMinBedroom() + " ");
        rawQuery.append("and p.floor >= " + buyEntity.getMinFloor() + " ");
        rawQuery.append("and p.horizontal >= " + buyEntity.getMinHorizontal() + " ");
        rawQuery.append("and p.vertical >= " + buyEntity.getMinVertical() + " ");


        rawQuery.append("and p.legalDocument in :legalDocuments ");
        rawQuery.append("and p.directionProperty in :directions ");
        rawQuery.append("and p.postStatus = 'OPENING' ");
        rawQuery.append("order by p.createdAt desc ");

        Query query = entityManager.createQuery(rawQuery.toString());
        query.setParameter("districtIds", buyEntity.getDistrictIds());
        query.setParameter("legalDocuments", buyEntity.getLegalDocuments());
        query.setParameter("directions", buyEntity.getDirectionProperties());
        query.setFirstResult(0);
        query.setMaxResults(10);
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
}