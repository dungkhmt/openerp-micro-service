package com.real_estate.post.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile({"postgresql"})
@EntityScan(basePackages = {"com.real_estate.common.models.postgres"})
public class PostgresConfig {
	@Value("#{'${postgres.url}'}")
	private String url;

	@Value("#{'${postgres.username}'}")
	private String username;

	@Value("#{'${postgres.password}'}")
	private String password;

	@Bean
	public DataSource dataSource(){
		return DataSourceBuilder.create()
			.url(url)
			.username(username)
			.password(password)
			.driverClassName("org.postgresql.Driver")
			.build();
	}
}
