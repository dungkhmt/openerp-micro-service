package com.hust.baseweb.service;

import com.hust.baseweb.entity.Content;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;

public interface ContentService {

    Content createContent(InputStream inputStream, String realName, String contentType) throws IOException;

    Response getContentData(String contentId) throws IOException;
}
