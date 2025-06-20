package com.hust.openerp.taskmanagement.repository;

import com.hust.openerp.taskmanagement.entity.OrganizationUser;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationUserRepository extends JpaRepository<OrganizationUser, OrganizationUser.OrganizationUserId> {
    @Query("SELECT ou FROM OrganizationUser ou WHERE ou.organizationId = :organizationId AND ou.thrsDate IS NULL")
    List<OrganizationUser> findAllActive(@Param("organizationId") UUID organizationId);

    boolean existsByUserId(String userId);

    Optional<OrganizationUser> findByUserId(String userId);

    @Modifying
    @Transactional
    @Query(value = """
    INSERT INTO task_management_organization_user (
        organization_id, organization_code, user_id, from_date, role_id, thrs_date
    ) VALUES (
        :organizationId, :organizationCode, :userId, :fromDate, :roleId, :thrsDate
    )
    """, nativeQuery = true)
    void insertOrganizationUser(@Param("organizationId") UUID organizationId,
                                @Param("organizationCode") String organizationCode,
                                @Param("userId") String userId,
                                @Param("fromDate") Date fromDate,
                                @Param("roleId") String roleId,
                                @Param("thrsDate") Date thrsDate);
}
