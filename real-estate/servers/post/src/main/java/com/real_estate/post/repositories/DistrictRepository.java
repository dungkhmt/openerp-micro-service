package com.real_estate.post.repositories;


import com.real_estate.post.dtos.ProvinceEntity;
import com.real_estate.post.models.postgresql.DistrictPostgresEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<DistrictPostgresEntity, String> {
	@Query("select new com.real_estate.post.dtos.ProvinceEntity(entity.provinceId, entity.nameProvince) " +
		"from DistrictPostgresEntity entity " +
		"group by entity.provinceId, entity.nameProvince " +
		"order by entity.nameProvince")
	public List<ProvinceEntity> findProvince();

	@Query("select entity from DistrictPostgresEntity entity " +
		"where entity.provinceId = :provinceId " +
		"order by entity.nameDistrict")
	public List<DistrictPostgresEntity> findBy(String provinceId);
}
