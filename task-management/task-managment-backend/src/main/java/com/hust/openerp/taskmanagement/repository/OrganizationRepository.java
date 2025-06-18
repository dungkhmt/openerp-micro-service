package com.hust.openerp.taskmanagement.repository;

import com.hust.openerp.taskmanagement.dto.projection.OrganizationOverviewProjection;
import com.hust.openerp.taskmanagement.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    @Query("""
         SELECT o.id as id,
         o.name as name,
         o.code as code,
         ou.roleId as myRole,
         (SELECT COUNT(*) FROM OrganizationUser ou2
          WHERE ou2.organizationId = o.id AND ou2.thrsDate IS NULL) as memberCount
         FROM OrganizationUser ou
         JOIN Organization o ON ou.organizationId = o.id
         WHERE ou.userId = :userId AND ou.thrsDate IS NULL
        """)
    List<OrganizationOverviewProjection> findOrganizationOverviewByUserId(@Param("userId") String userId);

    @Query("SELECT o FROM OrganizationUser ou JOIN ou.organization o WHERE ou.userId = :userId AND ou.thrsDate IS NULL ORDER BY o.createdStamp ASC LIMIT 1")
    Optional<Organization> findFirstOrganizationByUserId(@Param("userId") String userId);

    boolean existsByCode(String code);

    Optional<Organization> findByCode(String organizationCode);
}