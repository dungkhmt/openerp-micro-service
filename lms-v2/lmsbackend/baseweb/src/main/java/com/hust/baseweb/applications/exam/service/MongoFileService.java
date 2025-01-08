package com.hust.baseweb.applications.exam.service;

import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MongoFileService {

    String storeFile(MultipartFile file);

    List<String> storeFiles(MultipartFile[] files);

    GridFsResource getFile(String id);

    void deleteByPath(String path);
}
