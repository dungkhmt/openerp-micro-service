package com.example.api.configs;


import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Data
@EnableAsync
@Configuration
@EnableScheduling
public class AppConfig {
    private List<String> authorizedRedirectUris = new ArrayList<>();

//    @Autowired
//    private EntityManager em;
//
//    @Bean
//    public JPAQueryFactory jpaQueryFactory() {
//        return new JPAQueryFactory(em);
//    }
}
