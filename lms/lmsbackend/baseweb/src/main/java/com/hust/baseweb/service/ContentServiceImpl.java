package com.hust.baseweb.service;

import com.hust.baseweb.constant.ContentTypeConstant;
import com.hust.baseweb.entity.Content;
import com.hust.baseweb.repo.ContentRepo;
import com.hust.baseweb.repo.FileRepo;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentRepo contentRepo;
    @Autowired
    private FileRepo fileRepo;

    @Override
    @Transactional
    public Content createContent(InputStream inputStream, String realName, String contentType) throws IOException {

        Content content = new Content(ContentTypeConstant.DOCUMENT.name(), null, new Date());
        content = contentRepo.save(content);
        String url = fileRepo.create(inputStream, content.getContentId().toString(), realName, contentType);
        content.setUrl(url);
        content.setLastUpdatedAt(new Date());
        content = contentRepo.save(content);
        return content;
    }

    @Override
    public Response getContentData(String contentId) throws IOException {

        Content content = contentRepo.findById(UUID.fromString(contentId)).orElse(null);
        if (content != null) {
            return fileRepo.get(content.getUrl());
        }
        return null;
    }

}
