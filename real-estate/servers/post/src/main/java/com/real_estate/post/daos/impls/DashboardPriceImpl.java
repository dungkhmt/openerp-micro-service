package com.real_estate.post.daos.impls;

import com.real_estate.post.daos.interfaces.DashboardPriceDao;
import com.real_estate.post.dtos.PriceDistrict;
import com.real_estate.post.dtos.PriceDistrictQuery;
import com.real_estate.post.dtos.response.DashboardTopResponseDto;
import com.real_estate.post.models.DashboardPriceEntity;
import com.real_estate.post.models.postgresql.DashboardPricePostgresEntity;
import com.real_estate.post.repositories.DashboardPriceRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component("dashboardPriceImpl")
public class DashboardPriceImpl implements DashboardPriceDao {
    @Autowired
    ModelMapper mapper;

    @Autowired
    DashboardPriceRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DashboardPriceEntity save(DashboardPriceEntity entity) {
        DashboardPricePostgresEntity postgres = this.mapper.map(entity, DashboardPricePostgresEntity.class);
        postgres = repository.save(postgres);
        return mapper.map(postgres, DashboardPriceEntity.class);
    }

    @Override
    public List<DashboardPriceEntity> saveAll(List<DashboardPriceEntity> entities) {
        List<DashboardPricePostgresEntity> postgresEntities = entities.stream().map(entity -> {
            return mapper.map(entity, DashboardPricePostgresEntity.class);
        }).collect(Collectors.toList());
        postgresEntities = repository.saveAll(postgresEntities);
        return postgresEntities.stream().map(postgreEntity -> {
            return mapper.map(postgreEntity, DashboardPriceEntity.class);
        }).collect(Collectors.toList());
    }

    @Override
    public Long getLastTimeTrigger() {
        return repository.findLastTimeTrigger().orElse(0L);
    }

    @Override
    public List<DashboardPriceEntity> findBy(Long fromTime, Long toTime, String typeProperty, String districtId) {
        List<DashboardPricePostgresEntity> postgresEntities = repository.findBy(fromTime, toTime, typeProperty, districtId);
        return postgresEntities.stream().map(postEntity -> {
            return this.mapper.map(postEntity, DashboardPriceEntity.class);
        }).collect(Collectors.toList());
    }

    @Override
    public List<PriceDistrict> findPriceDistricts(List<PriceDistrictQuery> queries) {
        if (queries.isEmpty()) {
            return Collections.emptyList();
        }

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT pdq.districtId, pdq.nameDistrict,")
                .append(" MAX(CASE WHEN dpe.type_property = 'HOUSE' THEN dpe.medium_price_perm2 ELSE NULL END) AS mediumHouse,")
                .append(" MAX(CASE WHEN dpe.type_property = 'APARTMENT' THEN dpe.medium_price_perm2 ELSE NULL END) AS mediumApartment,")
                .append(" MAX(CASE WHEN dpe.type_property = 'LAND' THEN dpe.medium_price_perm2 ELSE NULL END) AS mediumLand,")
                .append(" COALESCE(SUM(dpe.total_post), 0) AS totalPost,")
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
                .append(" LEFT JOIN dashboard_price dpe")
                .append(" ON pdq.districtId = dpe.district_id AND pdq.nameDistrict = dpe.name_district AND pdq.startTime = dpe.start_time")
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
    public List<DashboardTopResponseDto> find5mediumHighest(String provinceId, String typeProperty, Long startTime) {
        Query query = entityManager.createQuery(
                "select db " +
                        "from DashboardPricePostgresEntity db " +
                        "join DistrictPostgresEntity d " +
                        "on db.districtId = d.districtId " +
                        "where d.provinceId = :provinceId " +
                        "and db.typeProperty = :typeProperty " +
                        "and db.startTime = :startTime " +
                        "order by db.mediumPricePerM2 desc limit 5");
        query.setParameter("provinceId", provinceId);
        query.setParameter("typeProperty", typeProperty);
        query.setParameter("startTime", startTime);
        List<DashboardPricePostgresEntity> postgresEntities = query.getResultList();
        return postgresEntities.stream().map(item -> {
            return new DashboardTopResponseDto(item.getDistrictId(), item.getNameDistrict(), item.getMediumPricePerM2());
        }).toList();
    }
}
