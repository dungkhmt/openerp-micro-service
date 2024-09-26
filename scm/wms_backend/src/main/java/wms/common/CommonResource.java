package wms.common;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
public class CommonResource {
    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;
    @Value("${graphhopper.apiKey}")
    private String apiKey;
    @Value("${graphhopper.url}")
    private String graphhopperUrl;
}
