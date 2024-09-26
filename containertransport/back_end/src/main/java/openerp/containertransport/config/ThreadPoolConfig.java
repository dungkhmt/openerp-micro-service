package openerp.containertransport.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfig {
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ExecutorService threadPoolCreateCSVFileForQuote() {
        int poolSize = 1;
        ExecutorService threadpool = Executors.newFixedThreadPool(poolSize);
        log.info("Init thread pool handle distant facility: {}, max_thread={}", threadpool, poolSize);
        return threadpool;
    }

}
