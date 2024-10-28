package com.hust.baseweb.config;

import lombok.AllArgsConstructor;
import org.springframework.content.fs.config.EnableFilesystemStores;
import org.springframework.content.fs.io.FileSystemResourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * @author Le Anh Tuan
 */
@Configuration
@EnableFilesystemStores
@AllArgsConstructor
public class SpringContent {

    private FileSystemStorageProperties properties;

    @Bean
    public File fileSystemRoot() {
        return new File(properties.getFilesystemRoot());
    }

    @Bean
    public FileSystemResourceLoader fileSystemResourceLoader() {
        return new FileSystemResourceLoader(fileSystemRoot().getAbsolutePath());
    }
}
