package com.real_estate.post.repositories;

import com.real_estate.post.models.postgresql.AccountPostgresEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountPostgresEntity, Long> {
    @Query("SELECT a FROM AccountPostgresEntity a where a.phone = :phone")
    public Optional<AccountPostgresEntity> findByPhone(String phone);

    @Query("SELECT a FROM AccountPostgresEntity a where a.email = :email")
    public Optional<AccountPostgresEntity> findByEmail(String email);
}
