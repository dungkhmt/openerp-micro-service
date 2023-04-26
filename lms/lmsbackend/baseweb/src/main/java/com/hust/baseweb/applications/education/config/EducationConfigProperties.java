package com.hust.baseweb.applications.education.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "")
public class EducationConfigProperties {
    @NotBlank
    private String url_root;
}
