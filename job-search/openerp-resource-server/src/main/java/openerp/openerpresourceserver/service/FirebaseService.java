package openerp.openerpresourceserver.service;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Objects;

@Service
public class FirebaseService {
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        String objectName = generateFileName(multipartFile);

        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream("C:\\Users\\ADMIN\\Desktop\\openerp-micro-service\\job-search\\openerp-resource-server\\datn-firebase.json"));
        File file = convertMultiPartToFile(multipartFile);
        Path filePath = file.toPath();

        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        BlobId blobId = BlobId.of("datn-b6f1c.appspot.com", objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(multipartFile.getContentType()).build();

        storage.create(blobInfo, Files.readAllBytes(filePath));
        String fileUrl = URI.create("https://firebasestorage.googleapis.com/v0/b/" + "datn-b6f1c.appspot.com" + "/o/" + objectName).toString();
        return fileUrl;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
    }

    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + Objects.requireNonNull(multiPart.getOriginalFilename()).replace(" ", "_");
    }
}