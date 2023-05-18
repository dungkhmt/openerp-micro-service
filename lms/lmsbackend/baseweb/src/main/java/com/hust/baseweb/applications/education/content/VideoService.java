package com.hust.baseweb.applications.education.content;


import com.hust.baseweb.applications.education.classmanagement.service.storage.exception.StorageException;
import com.hust.baseweb.config.FileSystemStorageProperties;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * @author Le Anh Tuan
 */
@Service
public class VideoService {

    private final String videoPath;

    private final VideoRepository filesRepo;

    @Autowired
    public VideoService(VideoRepository filesRepo, FileSystemStorageProperties properties) {
        this.videoPath = properties.getFilesystemRoot() + properties.getVideoPath();
        this.filesRepo = filesRepo;
    }

    @Transactional
    public Video create(MultipartFile file) throws IOException {
        return store(file, null);
    }

    @Transactional(readOnly = true)
    public Video update(UUID id, MultipartFile file) throws IOException {
        Video video = filesRepo.findByIdAndDeletedFalse(id).orElseThrow(NoSuchElementException::new);
        return store(file, video);
    }

    @Transactional
    public void delete(UUID id) {
        filesRepo.updateDeleted(id, true);
    }

    public void deleteVideo(UUID id) {
        File deleteFile = new File(videoPath + "/" + id);
        try {
            deleteFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public Video undelete(UUID id) {
        Video video = filesRepo.findById(id).orElseThrow(NoSuchElementException::new);

        video.setDeleted(false);
        filesRepo.save(video);
        return video;
    }

    /**
     * Create new or replace file in hard disk and save file metadata to database.
     *
     * @param file     new file
     * @param metadata file metadata in the case updating file
     * @return file metadata - a {@link Video} object
     * @throws IOException
     */
    @Transactional
    Video store(MultipartFile file, Video metadata) throws IOException {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        if (file.isEmpty()) {
            throw new StorageException("Failed to store empty file \"" + originalFileName + "\"");
        }

        if (originalFileName.contains("..")) {
            // This is a security check
            throw new StorageException("Cannot store file \"" + originalFileName +
                                       "\"with relative path outside current directory");
        }

        // Fulfill metadata.
        if (metadata == null) {
            metadata = new Video();
        }

        Tika tika = new Tika();
        String mimeType = tika.detect(file.getBytes());

        metadata.setOriginalName(originalFileName);
        metadata.setContentLength(file.getSize());
        metadata.setMimeType(mimeType);

        try {
            metadata.setExtension(getExtensionFromMimeType(mimeType));
        } catch (MimeTypeException e) {
            metadata.setExtension(".mp4");
        }

        // Save metadata.
        metadata = filesRepo.save(metadata);

        // Save file to hard disk.
        Path path = Paths.get(videoPath);
        Files.copy(
            file.getInputStream(),
            path.resolve(metadata.getId().toString()),
            StandardCopyOption.REPLACE_EXISTING);

        return metadata;
    }

    private String getExtensionFromMimeType(String mimeType) throws MimeTypeException {
        return MimeTypes.getDefaultMimeTypes().forName(mimeType).getExtension();
    }
}
