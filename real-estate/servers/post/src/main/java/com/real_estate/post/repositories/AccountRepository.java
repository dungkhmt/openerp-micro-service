package com.real_estate.post.repositories;

import com.real_estate.post.models.postgresql.AccountPostgresEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountPostgresEntity, Long> {
    @Query("SELECT a FROM AccountPostgresEntity a where a.phone = :phone")
    public Optional<AccountPostgresEntity> findByPhone(String phone);

    @Query("SELECT a FROM AccountPostgresEntity a where a.email = :email")
    public Optional<AccountPostgresEntity> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("update AccountPostgresEntity a set a.avatar = :avatar, a.phone = :phone, a.name = :name, a.updatedAt = :now where a.accountId = :accountId")
    public Integer updateAccount(String avatar, String phone,String name, Long now, Long accountId);

    @Modifying
    @Transactional
    @Query("update AccountPostgresEntity a set a.password = :newPassword where a.accountId = :accountId")
    public Integer updatePasswordBy(String newPassword, Long accountId);

    @Transactional
    @Modifying
    @Query(value = "update AccountPostgresEntity a set a.totalPostSell = a.totalPostSell + 1 where a.accountId = :accountId")
    void updateTotalPostSell(Long accountId);

    @Transactional
    @Modifying
    @Query(value = "update AccountPostgresEntity a set a.totalPostBuy = a.totalPostBuy + 1 where a.accountId = :accountId")
    void updateTotalPostBuy(Long accountId);

    @Transactional
    @Modifying
    @Query(value = "update AccountPostgresEntity a set a.password = :newPassword where a.email = :email and a.provider = 'local'")
    Integer updatePassByEmail(String newPassword, String email);
}
