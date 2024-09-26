package com.hust.baseweb.applications.education.classmanagement.service.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

@Service
public interface StorageService {

    void init(Path path);

    void store(MultipartFile file, String folder, String savedName) throws IOException;

    /*Stream<Path> loadAll();*/

    Path load(String fileName, String folder);

    InputStream loadFileAsResource(String fileName, String folder) throws IOException;

    /**
     * Delete file or directory.
     *
     * @param path
     * @return
     * @throws IOException
     */
    boolean deleteAll(Path path) throws IOException;

    void deleteIfExists(String folder, String fileName) throws IOException;

    void createFolder(String relPath2folder) throws IOException;
}
