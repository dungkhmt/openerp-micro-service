package com.real_estate.post.config;

import com.real_estate.post.handler.GlobalExceptionHandler;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
//	@Value("${JWT_SECRET}")
//	String jwtSecret;
//
//	@Value("${JWT_EXPIRATION}")
//	Long jwtExpires;
//
//	@Bean
//	public JwtTokenProvider jwtTokenProvider() {
//		return new JwtTokenProvider(jwtSecret, jwtExpires);
//	}
//	@Bean
//	public JwtAuthenticationFilter jwtAuthenticationFilter() {
//		return new JwtAuthenticationFilter(jwtTokenProvider());
//	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		return modelMapper;
	}

	@Bean
	public GlobalExceptionHandler globalExceptionHandler() {
		return new GlobalExceptionHandler();
	}
}
