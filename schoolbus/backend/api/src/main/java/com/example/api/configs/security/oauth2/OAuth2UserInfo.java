package com.example.api.configs.security.oauth2;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;

    public abstract String getId();
    public abstract String getUsername();
    public abstract String getEmail();
    public abstract String getName();
    public abstract String getImageUrl();

}
