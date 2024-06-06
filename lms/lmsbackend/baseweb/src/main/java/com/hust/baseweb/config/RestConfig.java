package com.hust.baseweb.config;


import com.hust.baseweb.rest.user.DPerson;
import com.hust.baseweb.rest.user.DPersonUserLogin;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Component
public class RestConfig implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        config.exposeIdsFor(DPerson.class);
        config.exposeIdsFor(DPersonUserLogin.class);
    }
}
