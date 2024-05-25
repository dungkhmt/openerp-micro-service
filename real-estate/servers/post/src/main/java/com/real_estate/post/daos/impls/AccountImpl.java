package com.real_estate.post.daos.impls;

import com.real_estate.post.daos.interfaces.AccountDao;
import com.real_estate.post.models.AccountEntity;
import com.real_estate.post.models.postgresql.AccountPostgresEntity;
import com.real_estate.post.repositories.AccountRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("accountImpl")
public class AccountImpl implements AccountDao {

    @Autowired
    AccountRepository repository;

    @Autowired
    ModelMapper mapper;

    @Override
    public Optional<AccountEntity> findByPhone(String phone) {
        Optional<AccountPostgresEntity> postgresEntityOptional = repository.findByPhone(phone);
        return postgresEntityOptional.map(postgresEntity -> mapper.map(postgresEntity, AccountEntity.class));
    }

    @Override
    public Optional<AccountEntity> findById(Long accountId) {
        Optional<AccountPostgresEntity> postgresEntityOptional = repository.findById(accountId);
        return postgresEntityOptional.map(postgresEntity -> mapper.map(postgresEntity, AccountEntity.class));
    }

    @Override
    public Optional<AccountEntity> findByEmail(String email) {
        Optional<AccountPostgresEntity> postgresEntityOptional = repository.findByEmail(email);
        return postgresEntityOptional.map(postgresEntity -> mapper.map(postgresEntity, AccountEntity.class));
    }


    @Override
    public AccountEntity save(AccountEntity account) {
        AccountPostgresEntity postgres = mapper.map(account, AccountPostgresEntity.class);
        postgres = repository.save(postgres);
        return mapper.map(postgres, AccountEntity.class);
    }

    @Override
    public Integer updateAccount(String avatar, String phone, String name, Long accountId) {
        Long now = System.currentTimeMillis();
        return repository.updateAccount(avatar, phone, name, now, accountId);
    }

    @Override
    public Integer updatePassword(String newPassword, Long accountId) {
        return repository.updatePasswordBy(newPassword, accountId);
    }

    @Override
    public void incOneTotalPostSellBy(Long accountId) {
        repository.updateTotalPostSell(accountId);
    }

    @Override
    public void incOneTotalPostBuyBy(Long accountId) {
        repository.updateTotalPostBuy(accountId);
    }
}
