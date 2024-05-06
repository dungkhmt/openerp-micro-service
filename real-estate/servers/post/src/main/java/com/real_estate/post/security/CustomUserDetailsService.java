package com.real_estate.post.security;

import com.real_estate.post.daos.interfaces.AccountDao;
import com.real_estate.post.models.AccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    @Qualifier("accountImpl")
    AccountDao accountDao;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        AccountEntity account = accountDao.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email : " + email)
        );

        return UserPrincipal.create(account);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        AccountEntity account = accountDao.findById(id)
                .orElseThrow(() ->
                                     new UsernameNotFoundException("User not found with accountId : " + id)
                );

        return UserPrincipal.create(account);
    }
}