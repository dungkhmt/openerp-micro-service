package com.real_estate.post.security.oauth2;

import com.real_estate.post.daos.interfaces.AccountDao;
import com.real_estate.post.handler.OAuth2AuthenticationProcessingException;
import com.real_estate.post.models.AccountEntity;
import com.real_estate.post.security.UserPrincipal;
import com.real_estate.post.security.oauth2.user.OAuth2UserInfo;
import com.real_estate.post.security.oauth2.user.OAuth2UserInfoFactory;
import com.real_estate.post.utils.AuthProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    @Qualifier("accountImpl")
    AccountDao accountDao;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        System.out.println("gia tri" + oAuth2User);
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        if(StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }
        System.out.println("bat dau tim voi email" + oAuth2UserInfo.getEmail());
        Optional<AccountEntity> accountEntityOptional = accountDao.findByEmail(oAuth2UserInfo.getEmail());
        AccountEntity account;
        if(accountEntityOptional.isPresent()) {
            account = accountEntityOptional.get();
            System.out.println("tim thay" + account);

            if(!account.getProvider().equals(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        account.getProvider() + " account. Please use your " + account.getProvider() +
                        " account to login.");
            }
            account = updateExistingUser(account, oAuth2UserInfo);
        } else {
            System.out.println("khong tim thay");
            account = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        return UserPrincipal.create(account, oAuth2User.getAttributes());
    }

    private AccountEntity registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        System.out.println(oAuth2UserInfo);

        Long now = System.currentTimeMillis();
        AccountEntity account = new AccountEntity();

        account.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
        account.setProviderId(oAuth2UserInfo.getId());
        account.setName(oAuth2UserInfo.getName());
        account.setEmail(oAuth2UserInfo.getEmail());
        account.setAvatar(oAuth2UserInfo.getImageUrl());
        account.setTotalPostSell(0);
        account.setTotalPostBuy(0);
        account.setIsActive(true);
        account.setRole(new HashSet<>(Collections.singleton("USER")));
        account.setCreatedAt(now);
        account.setUpdatedAt(now);
        return accountDao.save(account);
    }

    private AccountEntity updateExistingUser(AccountEntity existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setName(oAuth2UserInfo.getName());
        existingUser.setAvatar(oAuth2UserInfo.getImageUrl());
        return accountDao.save(existingUser);
    }

}
