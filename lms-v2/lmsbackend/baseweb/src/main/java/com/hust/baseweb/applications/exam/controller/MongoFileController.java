package com.hust.baseweb.applications.exam.controller;

import com.hust.baseweb.applications.exam.service.MongoFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/service")
@RequiredArgsConstructor
public class MongoFileController {

    private final MongoFileService mongoFileService;

    @GetMapping("/files/{fileId}/{fileName}")
    public ResponseEntity<Resource> filter(@PathVariable String fileId, @PathVariable String fileName) {
        GridFsResource resource = mongoFileService.getFile(fileId);
        return ResponseEntity.ok()
                             .contentType(MediaType.parseMediaType(resource.getContentType()))
                             .body(resource);
    }
}
