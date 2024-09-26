package com.hust.baseweb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /* @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //registry.addViewController("/").setViewName("index");
    } */

    @Autowired
    FileSystemStorageProperties fs;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        System.out.println(
            "==================================================================================================");
        registry
            .addResourceHandler("/resources/backlog/**")
            .addResourceLocations("file:///" + fs.getFilesystemRoot() + fs.getBacklogDataPath());
    }

}
