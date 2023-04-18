package com.hust.baseweb.applications.education.thesisdefensejury.repo;

import com.hust.baseweb.applications.education.thesisdefensejury.entity.ThesisDefensePlan;
import com.hust.baseweb.applications.education.thesisdefensejury.entity.ThesisKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ThesisKeywordRepo extends JpaRepository<ThesisKeyword, String> {
    @Modifying
    @Query(value = "DELETE FROM thesis_keyword tk WHERE tk.thesis_id = :thesisId ", nativeQuery = true)
    void deleteByThesisId(UUID thesisId);

}
