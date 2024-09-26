package com.example.shared.db.repo;

import com.example.shared.db.dto.GetListRequestRegistrationDTO;
import com.example.shared.db.entities.RequestRegistration;
import com.example.shared.enumeration.RequestRegistrationStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RequestRegistrationRepository extends JpaRepository<RequestRegistration, Long> {
    List<RequestRegistration> findByParentIdOrderByCreatedAtDesc(Long parentId);

    List<RequestRegistration> findByParentIdAndStudentIdInAndStatus(
        Long parentId,
        List<Long> studentIds,
        RequestRegistrationStatus status
    );

    @Query("""
    SELECT r.student as student, r.parent as parent, r as requestRegistration
        FROM RequestRegistration r
        WHERE (:studentName IS NULL OR r.student.name ILIKE %:studentName%)
        AND (:parentName IS NULL OR r.parent.name ILIKE %:parentName%)
        AND (:statuses IS NULL OR r.status IN (:statuses))
        AND (:address IS NULL OR r.address ILIKE %:address%)
    """)
    Page<GetListRequestRegistrationDTO> getPageRequestRegistration(
        @Param("studentName") String studentName,
        @Param("parentName") String parentName,
        @Param("statuses") List<RequestRegistrationStatus> statuses,
        @Param("address") String address,
        Pageable pageable
    );
}
