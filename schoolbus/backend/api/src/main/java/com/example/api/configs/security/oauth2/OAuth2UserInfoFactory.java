package com.example.api.configs.security.oauth2;

import com.example.shared.enumeration.AuthProvider;
import com.example.shared.exception.OAuth2AuthenticationProcessingException;
import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) throws OAuth2AuthenticationProcessingException {
        if(registrationId.equalsIgnoreCase(AuthProvider.GOOGLE.getValue())){
            return new GoogleOAuth2UserInfo(attributes);
        }
        else{
            throw new OAuth2AuthenticationProcessingException("Login with " + registrationId +" is not support yet");
        }
    }
}
