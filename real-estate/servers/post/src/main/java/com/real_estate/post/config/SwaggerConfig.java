package com.real_estate.post.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

//@SecurityScheme(
//	name = "Bearer Authentication",
//	type = SecuritySchemeType.HTTP,
//	bearerFormat = "JWT",
//	scheme = "bearer"
//)
//@OpenAPIDefinition(
//	servers = {
//		@Server(url = "${springdoc.server}")
//	}
//)
@Configuration
public class SwaggerConfig {
	@Value("${springdoc.server}")
	String springDocServer;

	@Bean
	public OpenAPI customOpenAPI() {

		return new OpenAPI()
				.info(new Info().title("JavaInUse Authentication Service"))
				.addSecurityItem(new SecurityRequirement().addList("JavaInUseSecurityScheme"))
				.components(new Components().addSecuritySchemes("JavaInUseSecurityScheme", new SecurityScheme()
						.name("JavaInUseSecurityScheme").type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));

	}

//	@Bean
//	public OpenAPI openAPI() {
//		OpenAPI openAPI = new OpenAPI();
//		openAPI.setServers(List.of(new io.swagger.v3.oas.models.servers.Server().url(springDocServer)));
//		openAPI.schemaRequirement(
//				"Bearer Authentication",
//				new io.swagger.v3.oas.models.security.SecurityScheme()
//						.type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
//						.scheme("bearer")
//						.bearerFormat("JWT"));
//		return openAPI;
//	}
}
