package vn.edu.hust.soict.judge0client.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;


@Getter
@AllArgsConstructor
@ConstructorBinding
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@ConfigurationProperties(prefix = "judge0")
public class Judge0Config {

    String uri;

    Auth authn;

    Auth authz;

    Submission submission;

    @Getter
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class Auth {

        String header;

        String token;

    }

    @Getter
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class Submission {

        Float maxCpuTimeLimit;

        Float maxCpuExtraTime;

        Float maxWallTimeLimit;

        Integer maxMemoryLimit;

        Integer maxStackLimit;

        Integer maxMaxFileSize;

    }
}