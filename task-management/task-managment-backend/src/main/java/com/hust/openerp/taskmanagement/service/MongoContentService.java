package com.hust.openerp.taskmanagement.service;

import java.io.IOException;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.multipart.MultipartFile;

import com.hust.openerp.taskmanagement.model.ContentModel;

public interface MongoContentService {
    public ObjectId storeFileToGridFs(ContentModel contentModel) throws IOException;

    public GridFsResource getById(String id);

    public void deleteFilesById(String id);

    List<String> storeFiles(MultipartFile[] files);
}
