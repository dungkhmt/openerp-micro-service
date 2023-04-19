package com.hust.baseweb.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "content.fs")
/**
 * Properties of this class are immutable.
 * @author Le Anh Tuan
 */
public class FileSystemStorageProperties {

    @NotBlank
    private String filesystemRoot;

    @NotBlank
    private String videoPath;

    @NotBlank
    private String classManagementDataPath;

    @NotBlank
    private String backlogDataPath;
}
