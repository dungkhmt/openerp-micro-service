package com.hust.baseweb.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@AllArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "spring.mail")
/**
 * @author Le Anh Tuan
 */
public class MailProperties {

    //    @NotBlank
    private String username;
}
