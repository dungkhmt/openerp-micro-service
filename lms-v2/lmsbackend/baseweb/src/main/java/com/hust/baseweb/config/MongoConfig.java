package com.hust.baseweb.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
@Configuration
@EnableMongoRepositories(basePackages = {
    "com.hust.baseweb.applications.accounting.repo",
    "com.hust.baseweb.applications.tms.repo",
    "com.hust.baseweb.applications.order.repo.mongodb",
    "com.hust.baseweb.applications.webcam.repository",
    "com.hust.baseweb.applications.gismap.repo",
    "com.hust.baseweb.applications.specialpurpose.saleslogmongo.repository",
    "com.hust.baseweb.applications.education.repo.mongodb",
    "com.hust.baseweb.applications.education.suggesttimetable.repo"
})
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Bean
//    @Autowired
//    @ConditionalOnExpression("'${mongo.transactions}'=='enabled'")
    MongoTransactionManager transactionManager(MongoDbFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Value("${spring.data.mongodb.uri}")
    private String uri;


    @Override
    public MongoClient mongoClient() {
        return MongoClients.create(uri);
    }

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }
}
