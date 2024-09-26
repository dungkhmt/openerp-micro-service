package com.hust.openerp.taskmanagement.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(info = @Info(title = "OpenERP - Task Management", description = "APIs description for module TM", version = "v1"))
@SecurityScheme(name = OpenApiConfig.SECURITY_SCHEME_NAME, scheme = "Bearer", type = SecuritySchemeType.OAUTH2, flows = @OAuthFlows(password = @OAuthFlow(authorizationUrl = "${springdoc.oAuthFlow.authorizationUrl}", tokenUrl = "${springdoc.oAuthFlow.tokenUrl}")))
public class OpenApiConfig {
    public static final String SECURITY_SCHEME_NAME = "security_auth";
}