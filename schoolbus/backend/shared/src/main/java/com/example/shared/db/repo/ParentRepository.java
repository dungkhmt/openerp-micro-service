package com.example.shared.db.repo;

import com.example.shared.db.dto.GetParentAndChildDTO;
import com.example.shared.db.entities.Parent;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParentRepository extends JpaRepository<Parent, Long> {
    @Query(value = """
    select p
    from Parent p
    left join Account a on a.id = p.account.id
    left join Student s on s.parent.id = p.id
    where (:id is null or p.id = :id)
    and (:role is null or a.role = :role)
    and :searchType is null or :name is null or :name = '' or 
        (CASE 
            WHEN :searchType = 'PARENT_NAME' THEN p.name is null or p.name ILIKE %:name%
            WHEN :searchType = 'STUDENT_NAME' THEN s.name is null or s.name ILIKE %:name%
            WHEN :searchType = 'PARENT_PHONE_NUMBER' THEN p.phoneNumber is null or p.phoneNumber ILIKE %:name%
            ELSE FALSE
        END)
    and (:phoneNumber is null or p.phoneNumber like %:phoneNumber%)
    and (:studentId is null or s.id = :studentId)
        """)
    Page<Parent> searchPageParent(
        @Param("id") Long id,
        @Param("name") String name,
        @Param("role") String role,
        @Param("searchType") String searchType,
        @Param("phoneNumber") String phoneNumber,
        @Param("studentId") Long studentId,
        Pageable pageable
    );

    @Query(value = """
            select p as parent
            from Parent p
            where p.account.id = :accountId
        """)
    Optional<Parent> findByAccountId(Long accountId);
}
