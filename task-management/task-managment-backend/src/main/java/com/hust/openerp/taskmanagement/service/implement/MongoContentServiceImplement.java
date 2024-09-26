package com.hust.openerp.taskmanagement.service.implement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hust.openerp.taskmanagement.model.ContentModel;
import com.hust.openerp.taskmanagement.service.MongoContentService;
import com.mongodb.client.gridfs.model.GridFSFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MongoContentServiceImplement implements MongoContentService {
    private final GridFsOperations operations;

    public ObjectId storeFileToGridFs(ContentModel contentModel) throws IOException {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("upload_file_name", contentModel.getFile().getOriginalFilename());
        return operations.store(
                contentModel.getFile().getInputStream(),
                contentModel.getId(),
                contentModel.getFile().getContentType(),
                metadata);
    }

    public GridFsResource getById(String id) {
        GridFSFile fID = operations.findOne(Query.query(Criteria.where("_id").is(id)));
        if (fID == null) {
            return null;
        }
        return operations.getResource(fID);
    }

    public void deleteFilesById(String id) {
        operations.delete(Query.query(Criteria.where("_id").is(id)));
    }

    @Override
    public List<String> storeFiles(MultipartFile[] files) {
        List<String> storageIds = new ArrayList<>(files.length);

        for (MultipartFile file : files) {
            String uniqueFileName = new StringBuilder(UUID.randomUUID().toString())
                    .append("_").append(file.getOriginalFilename())
                    .toString();
            ContentModel contentModel = new ContentModel(uniqueFileName, file);
            try {
                ObjectId id = storeFileToGridFs(contentModel);
                storageIds.add(id.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return storageIds;
    }
}
