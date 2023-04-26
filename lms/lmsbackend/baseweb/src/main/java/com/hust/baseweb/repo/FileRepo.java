package com.hust.baseweb.repo;

import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;

public interface FileRepo {

    String create(InputStream input, String name, String realName, String contentType) throws IOException;

    Response get(String url) throws IOException;

}
