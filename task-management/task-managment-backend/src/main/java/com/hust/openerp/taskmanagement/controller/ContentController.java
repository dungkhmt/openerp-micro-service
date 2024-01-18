package com.hust.openerp.taskmanagement.controller;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hust.openerp.taskmanagement.model.ContentHeaderModel;
import com.hust.openerp.taskmanagement.model.ContentModel;
import com.hust.openerp.taskmanagement.service.MongoContentService;
import com.nimbusds.jose.shaded.gson.Gson;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ContentController {
    private final MongoContentService mongoContentService;

    @PostMapping("/content/create")
    public ResponseEntity<?> create(
            @RequestParam("inputJson") String inputJson,
            @RequestParam("file") MultipartFile file) {
        Gson gson = new Gson();
        ContentHeaderModel modelHeader = gson.fromJson(inputJson, ContentHeaderModel.class);
        ContentModel model = new ContentModel(modelHeader.getId(), file);

        ObjectId id;
        try {
            id = mongoContentService.storeFileToGridFs(model);
            ContentHeaderModel rs = new ContentHeaderModel(id.toHexString());
            return ResponseEntity.ok().body(rs);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(null);
        }
    }

    @GetMapping("/content/get/{id}")
    public ResponseEntity<byte[]> get(@PathVariable String id) {
        try {
            GridFsResource content = mongoContentService.getById(id);
            if (content != null) {
                InputStream inputStream = content.getInputStream();
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Type", content.getContentType());
                headers.add("Content-Disposition", "attachment");
                headers.add("Access-Control-Expose-Headers", "Content-Disposition");
                return new ResponseEntity<>(IOUtils.toByteArray(inputStream), headers, HttpStatus.OK);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
