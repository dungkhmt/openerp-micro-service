
package com.hust.baseweb.applications.contentmanager.controller;

import java.io.IOException;
import java.io.InputStream;


import com.google.gson.Gson;
import com.hust.baseweb.applications.contentmanager.model.ContentHeaderModel;
import com.hust.baseweb.applications.contentmanager.model.ContentModel;
import com.hust.baseweb.applications.contentmanager.repo.MongoContentService;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.extern.java.Log;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Log
public class ContentApiController {
    @Autowired
    private MongoContentService mongoContentService;

    @PostMapping("/content/create")
    public ResponseEntity<?> create(@RequestParam("inputJson") String inputJson,
                                         @RequestParam("file") MultipartFile file) {
        Gson gson = new Gson();
        ContentHeaderModel modelHeader = gson.fromJson(inputJson, ContentHeaderModel.class);
        ContentModel model = new ContentModel(modelHeader.getId(),file);

        ObjectId id;
        try {
            id = mongoContentService.storeFileToGridFs(model);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return ResponseEntity.ok().body(null);
        }
        log.info("create, returned id = " + id.toHexString());
        ContentHeaderModel rs = new ContentHeaderModel(id.toHexString());
        //return ResponseEntity.ok().body("" + id.toHexString());
        return ResponseEntity.ok().body(rs);
    }

    @GetMapping("/content/get/{id}")
    public ResponseEntity<byte[]> get(@PathVariable String id) {
        log.info("get data for " + id);
        try {
            GridFsResource content = mongoContentService.getById(id);
            if (content != null) {
                InputStream inputStream = content.getInputStream();
                HttpHeaders headers= new HttpHeaders();
//                headers.setContentType(MediaType.parseMediaType( content.getContentType()));
                headers.add("Content-Type", content.getContentType());
                headers.add("Content-Disposition", "attachment");
                headers.add("Access-Control-Expose-Headers", "Content-Disposition");
                return new ResponseEntity<>(IOUtils.toByteArray(inputStream), headers, HttpStatus.OK);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

