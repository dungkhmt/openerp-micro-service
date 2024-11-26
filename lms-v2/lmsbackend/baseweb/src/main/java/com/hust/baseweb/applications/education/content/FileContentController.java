package com.hust.baseweb.applications.education.content;

import com.hust.baseweb.applications.contentmanager.repo.MongoContentService;
import com.hust.baseweb.config.FileSystemStorageProperties;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@RestController
@AllArgsConstructor
public class FileContentController {

    private VideoService videoService;
    private FileSystemStorageProperties properties;
    @Autowired
    MongoContentService mongoContentService;

    @PostMapping("/files")
    public ResponseEntity<?> create(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(videoService.create(file));
    }

    @PutMapping("/files/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestParam("file") MultipartFile file)
        throws IOException {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(videoService.update(id, file));
        } catch (NoSuchElementException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/files/{id}")
    public ResponseEntity delete(@PathVariable UUID id) {
        videoService.delete(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/files/{id}/undelete")
    public ResponseEntity<?> undelete(@PathVariable UUID id) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(videoService.undelete(id));
        } catch (NoSuchElementException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/get-slide", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getFilesFromMongoById(@RequestBody Map<String, String> fileId) {
        String fileIds = fileId.get("fileId").toString();
        List<byte[]> files = new ArrayList<>();
        try {
            GridFsResource content = mongoContentService.getById(fileIds);
            if (content != null) {
                InputStream inputStream = content.getInputStream();
                //change file pdf to picture
                PDDocument document = PDDocument.load(inputStream);
                PDFRenderer pdfRenderer = new PDFRenderer(document);

                //check if folder slides is existing
                File slidesDir = new File(properties.getFilesystemRoot() + "/slides/");
                if (!slidesDir.exists()) {
                    slidesDir.mkdirs();
                }

                //change pdf format to png
                int numberOfPages = document.getNumberOfPages();
                System.out.println("Total files to be converting -> " + numberOfPages);

                String fileName = content.getFilename().replace(".pdf", "");
                String fileExtension = "png";

                int dpi = 120;  //for render speed
                for (int i = 0; i < numberOfPages; ++i) {
                    File outPutFile = new File(properties.getFilesystemRoot() +
                                               "/slides/" +
                                               fileName +
                                               "_" +
                                               (i + 1) +
                                               "." +
                                               fileExtension);
                    BufferedImage bImage = pdfRenderer.renderImageWithDPI(i, dpi, ImageType.RGB);
                    ImageIO.write(bImage, fileExtension, outPutFile);

                    // change type File to type FileInputStream
                    try(FileInputStream input = new FileInputStream(outPutFile)) {
                        //change image to byte and add to array
                        files.add(IOUtils.toByteArray(input));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //delete image
                    try {
                        outPutFile.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        if (fileIds.length != 0) {
//            for (String s : fileIds) {
//                try {
//                    GridFsResource content = mongoContentService.getById(s);
//                    if (content != null) {
//                        InputStream inputStream = content.getInputStream();
//                        files.add(IOUtils.toByteArray(inputStream));
//                    }
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        }
        return ResponseEntity.status(HttpStatus.OK).body(files);
    }
}
