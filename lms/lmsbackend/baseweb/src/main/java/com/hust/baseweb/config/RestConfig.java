package com.hust.baseweb.config;


import com.hust.baseweb.rest.user.DPerson;
import com.hust.baseweb.rest.user.DPersonUserLogin;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.stereotype.Component;

@Component
public class RestConfig implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration restConfig) {
        restConfig.exposeIdsFor(DPerson.class);
        restConfig.exposeIdsFor(DPersonUserLogin.class);
    }
}
