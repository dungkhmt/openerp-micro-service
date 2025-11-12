package com.real_estate.post.daos.impls;

import com.real_estate.post.daos.interfaces.DashboardDao;
import com.real_estate.post.dtos.PriceDistrict;
import com.real_estate.post.dtos.PriceDistrictQuery;
import com.real_estate.post.dtos.response.DashboardTopResponseDto;
import com.real_estate.post.models.DashboardEntity;
import com.real_estate.post.models.postgresql.DashboardPostgresEntity;
import com.real_estate.post.repositories.DashboardRepository;
import com.real_estate.post.utils.TypeProperty;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component("dashboardImpl")
public class DashboardImpl implements DashboardDao {
    @Autowired
    ModelMapper mapper;

    @Autowired
    DashboardRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DashboardEntity save(DashboardEntity entity) {
        DashboardPostgresEntity postgres = this.mapper.map(entity, DashboardPostgresEntity.class);
        postgres = repository.save(postgres);
        return mapper.map(postgres, DashboardEntity.class);
    }

    @Override
    public List<DashboardEntity> saveAll(List<DashboardEntity> entities) {
        List<DashboardPostgresEntity> postgresEntities = entities.stream().map(entity -> {
            return mapper.map(entity, DashboardPostgresEntity.class);
        }).collect(Collectors.toList());
        postgresEntities = repository.saveAll(postgresEntities);
        return postgresEntities.stream().map(postgreEntity -> {
            return mapper.map(postgreEntity, DashboardEntity.class);
        }).collect(Collectors.toList());
    }

    @Override
    public Long getLastTimeTrigger() {
        return repository.findLastTimeTrigger().orElse(0L);
    }

    @Override
    public List<DashboardEntity> findBy(Long fromTime, Long toTime, TypeProperty typeProperty, String districtId) {
        List<DashboardPostgresEntity> postgresEntities = repository.findBy(fromTime, toTime, typeProperty, districtId);
        return postgresEntities.stream().map(postEntity -> {
            return this.mapper.map(postEntity, DashboardEntity.class);
        }).collect(Collectors.toList());
    }

    @Override
    public List<PriceDistrict> findPriceDistricts(List<PriceDistrictQuery> queries) {
        if (queries.isEmpty()) {
            return Collections.emptyList();
        }

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT pdq.districtId, pdq.nameDistrict,")
                .append(" MAX(CASE WHEN db.type_property = 'HOUSE' THEN db.medium_price_per_m2 ELSE NULL END) AS mediumHouse,")
                .append(" MAX(CASE WHEN db.type_property = 'APARTMENT' THEN db.medium_price_per_m2 ELSE NULL END) AS mediumApartment,")
                .append(" MAX(CASE WHEN db.type_property = 'LAND' THEN db.medium_price_per_m2 ELSE NULL END) AS mediumLand,")
                .append(" COALESCE(SUM(db.total_post), 0) AS totalPost,")
                .append(" pdq.startTime")
                .append(" FROM (VALUES ");

        // Construct the VALUES part of the query
        for (int i = 0; i < queries.size(); i++) {
            PriceDistrictQuery query = queries.get(i);
            queryBuilder.append(String.format("('%s', '%s', %d)", query.getDistrictId(), query.getNameDistrict(), query.getStartTime()));
            if (i < queries.size() - 1) {
                queryBuilder.append(", ");
            }
        }

        queryBuilder.append(") AS pdq(districtId, nameDistrict, startTime)")
                .append(" LEFT JOIN dashboard db")
                .append(" ON pdq.districtId = db.district_id AND pdq.nameDistrict = db.name_district AND pdq.startTime = db.start_time")
                .append(" GROUP BY pdq.districtId, pdq.nameDistrict, pdq.startTime")
                .append(" ORDER BY pdq.startTime ASC");;

        Query query = entityManager.createNativeQuery(queryBuilder.toString());

        List<Object[]> results = query.getResultList();
        List<PriceDistrict> priceDistricts = new ArrayList<>();

        for (Object[] result : results) {
            PriceDistrict priceDistrict = new PriceDistrict();
            priceDistrict.setDistrictId((String) result[0]);
            priceDistrict.setNameDistrict((String) result[1]);
            priceDistrict.setMediumHouse((Double) result[2]);
            priceDistrict.setMediumApartment((Double) result[3]);
            priceDistrict.setMediumLand((Double) result[4]);
            priceDistrict.setTotalPost(((Number) result[5]).longValue());
            priceDistrict.setStartTime((Long) result[6]);
            priceDistricts.add(priceDistrict);
        }

        return priceDistricts;
    }

    @Override
    public List<DashboardTopResponseDto> find5mediumHighest(String provinceId, TypeProperty typeProperty, Long startTime) {
        Query query = entityManager.createQuery(
                "select db " +
                        "from DashboardPostgresEntity db " +
                        "join DistrictPostgresEntity d " +
                        "on db.districtId = d.districtId " +
                        "where d.provinceId = :provinceId " +
                        "and db.typeProperty = :typeProperty " +
                        "and db.startTime = :startTime " +
                        "order by db.mediumPricePerM2 desc limit 5");
        query.setParameter("provinceId", provinceId);
        query.setParameter("typeProperty", typeProperty);
        query.setParameter("startTime", startTime);
        List<DashboardPostgresEntity> postgresEntities = query.getResultList();
        return postgresEntities.stream().map(item -> {
            return new DashboardTopResponseDto(item.getDistrictId(), item.getNameDistrict(), item.getMediumPricePerM2());
        }).toList();
    }
}
