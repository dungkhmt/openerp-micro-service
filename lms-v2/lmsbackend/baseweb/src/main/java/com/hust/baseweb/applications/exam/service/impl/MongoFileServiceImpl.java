package com.hust.baseweb.applications.exam.service.impl;

import com.hust.baseweb.applications.exam.service.MongoFileService;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class MongoFileServiceImpl implements MongoFileService {

    private final GridFsOperations operations;

    @Override
    public String storeFile(MultipartFile file) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("upload_file_name", file.getOriginalFilename());

        try {
            ObjectId objectId = operations.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType(),
                metadata
            );

            return objectId.toHexString() + "/" + file.getOriginalFilename();
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<String> storeFiles(MultipartFile[] files) {
        List<String> paths = new ArrayList<>(files.length);

        for (MultipartFile file : files) {
            try {
                String path = storeFile(file);
                paths.add(path);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return paths;
    }

    @Override
    public GridFsResource getFile(String id) {
        GridFSFile file = operations.findOne(Query.query(Criteria.where("_id").is(id)));
        if (file == null) {
            return null;
        }
        return operations.getResource(file);
    }

    @Override
    public void deleteByPath(String path) {
        String[] paths = path.split("/");
        operations.delete(Query.query(Criteria.where("_id").is(paths[paths.length-2])));
    }
}
