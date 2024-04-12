package com.real_estate.post.daos.impls;


import com.real_estate.common.models.DistrictEntity;
import com.real_estate.common.models.postgres.DistrictPostgresEntity;
import com.real_estate.post.daos.interfaces.DistrictDao;
import com.real_estate.post.dtos.ProvinceEntity;
import com.real_estate.post.repositories.DistrictRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("districtImpl")
public class DistrictImpl implements DistrictDao {
	@Autowired
	ModelMapper mapper;

	@Autowired
	DistrictRepository repository;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void saveAll(List<DistrictEntity> entities) {
		List<DistrictPostgresEntity> postgresEntities = entities.stream().map(entity -> {
			return this.mapper.map(entity, DistrictPostgresEntity.class);
		}).collect(Collectors.toList());
		repository.saveAll(postgresEntities);
	}

	@Override
	public List<ProvinceEntity> findDistinctProvince() {
		return repository.findProvince();
	}

	@Override
	public List<DistrictEntity> findDistrictsBy(String nameProvince) {
		List<DistrictPostgresEntity> postgresEntities = repository.findBy(nameProvince);
		return postgresEntities.stream().map((postgresEntity) -> {
			return this.mapper.map(postgresEntity, DistrictEntity.class);
		}).collect(Collectors.toList());
	}
}
