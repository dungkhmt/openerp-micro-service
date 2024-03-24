package com.example.api.services.auth;

import com.example.api.configs.security.CustomUserDetails;
import com.example.api.configs.security.oauth2.OAuth2UserInfo;
import com.example.api.configs.security.oauth2.OAuth2UserInfoFactory;
import com.example.shared.db.entities.Account;
import com.example.shared.db.repo.AccountRepository;
import com.example.shared.enumeration.AuthProvider;
import com.example.shared.enumeration.UserRole;
import com.example.shared.exception.OAuth2AuthenticationProcessingException;
import java.io.Serializable;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2Service extends DefaultOAuth2UserService implements Serializable {
    private final AccountRepository accountRepository;
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

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User)
            throws OAuth2AuthenticationProcessingException {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                oAuth2UserRequest.getClientRegistration().getRegistrationId(),
                oAuth2User.getAttributes()
        );

        Optional<Account> userOptional = accountRepository.findByUsername(oAuth2UserInfo.getEmail());
        Account user;
        if(userOptional.isPresent()){
            user = userOptional.get();
            user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
        }else{
            user = signUpNewUser(oAuth2UserRequest,oAuth2UserInfo);
        }
        return new CustomUserDetails(user, oAuth2User.getAttributes());
    }

    private Account signUpNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo){

        Account account = Account.builder()
            .provider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))
            .username(oAuth2UserInfo.getEmail())
            .role(UserRole.CLIENT)
            .build();

        return accountRepository.save(account);
    }

    private Account updateExistingUser(Account existingUser, OAuth2UserInfo oAuth2UserInfo) {
        return accountRepository.save(existingUser);
    }
}
