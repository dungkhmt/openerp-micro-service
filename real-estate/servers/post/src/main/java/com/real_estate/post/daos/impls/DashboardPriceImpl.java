package com.real_estate.post.daos.impls;

import com.real_estate.post.daos.interfaces.DashboardPriceDao;
import com.real_estate.post.models.DashboardPriceEntity;
import com.real_estate.post.models.postgresql.DashboardPricePostgresEntity;
import com.real_estate.post.repositories.DashboardPriceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("dashboardPriceImpl")
public class DashboardPriceImpl implements DashboardPriceDao {
    @Autowired
    ModelMapper mapper;

    @Autowired
    DashboardPriceRepository repository;

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
}
