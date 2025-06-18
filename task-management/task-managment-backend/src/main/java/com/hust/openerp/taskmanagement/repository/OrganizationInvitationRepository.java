package com.hust.openerp.taskmanagement.repository;

import com.hust.openerp.taskmanagement.entity.OrganizationInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationInvitationRepository extends JpaRepository<OrganizationInvitation, UUID> {
    @Query(
        value = "SELECT * FROM task_management_organization_invitation WHERE status_id = :statusId AND expiration_time < :date",
        nativeQuery = true
    )
    List<OrganizationInvitation> findByStatusIdAndExpirationTimeBefore(String statusId, Date date);

    boolean existsByOrganizationIdAndEmailAndStatusId(UUID organizationId, String email, String statusId);

    @Query(value = """
        SELECT oi.* 
        FROM task_management_organization_invitation oi
        WHERE oi.token = :token
        """, nativeQuery = true)
    Optional<OrganizationInvitation> findByToken(@Param("token") String token);

    @Query(value = """
        SELECT oi.* 
        FROM task_management_organization_invitation oi
        WHERE oi.email = :email AND oi.status_id = :statusId
        """, nativeQuery = true)
    List<OrganizationInvitation> findByEmailAndStatusId(@Param("email") String email,
                                                        @Param("statusId") String statusId);

    @Query("SELECT oi FROM OrganizationInvitation oi " +
        "JOIN oi.inviter " +
        "JOIN oi.organization " +
        "JOIN oi.status " +
        "WHERE oi.statusId = :statusId")
    List<OrganizationInvitation> findByStatusId(@Param("statusId") String statusId);
}
