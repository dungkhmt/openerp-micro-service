package com.example.api.configs.security.oauth2;

import com.example.api.configs.AppConfig;
import com.example.api.configs.security.CustomUserDetails;
import com.example.api.utils.JwtUtil;
import com.example.shared.db.entities.Account;
import com.example.shared.utils.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    private final AppConfig appConfig;

    @Value("${app.authorizedRedirectUris}")
    private String targetUrl;



    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("Login on success");

        String targetUrl = determineTargetUrl(request,response,authentication);

//        response.setHeader("Authorization", "Bearer " + targetUrl);
        clearAuthenticationAttributes(request,response);

//        targetUrl = "http://localhost:3000/oauth2/callback";

        getRedirectStrategy().sendRedirect(request,response,targetUrl);

    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication){
        Optional<String> redirectUri = CookieUtils.getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);
//        if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())){
//            throw new BadRequestException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
//        }
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        Account account = customUserDetails.toAccount();
        String token = JwtUtil.generateAccessToken(account);
        String refreshToken = JwtUtil.generateRefreshToken(account);
        CookieUtils.addCookie(response,"token",token,9999999);
        CookieUtils.addCookie(response,"refreshToken",refreshToken,9999999);

        return UriComponentsBuilder
            .fromUriString(targetUrl)
            .queryParam("token", token)
            .queryParam("refreshToken", refreshToken)
            .build()
            .toUriString();

    }
    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri){
        URI clientRedirectUri = URI.create(uri);
        return appConfig.getAuthorizedRedirectUris().stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    if(authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())&&
                    authorizedURI.getPort()== clientRedirectUri.getPort()){
                        return true;
                    }
                    return false;
                });
    }

}
