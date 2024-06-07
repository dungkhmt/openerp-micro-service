package com.real_estate.post.daos.interfaces;

import com.real_estate.post.models.AccountEntity;

import java.util.Optional;

public interface AccountDao {
    public Optional<AccountEntity> findByPhone(String phone);

    public Optional<AccountEntity> findById(Long accountId);

    public Optional<AccountEntity> findByEmail(String email);

    public AccountEntity save(AccountEntity account);

    public Integer updateAccount(String avatar, String phone, String name, Long accountId);

    public Integer updatePassword(String newPassword, Long accountId);

    public Integer updatePassByEmail(String newPassword, String email);

    void incOneTotalPostSellBy(Long accountId);

    void incOneTotalPostBuyBy(Long accountId);

}
