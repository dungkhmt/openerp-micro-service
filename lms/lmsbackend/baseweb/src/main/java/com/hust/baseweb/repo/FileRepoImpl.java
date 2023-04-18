package com.hust.baseweb.repo;

import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Repository
public class FileRepoImpl implements FileRepo {

    @Value("${content-repo.url}")
    private String contentRepoUrl;
    private static final MediaType MEDIA_TYPE_IMAGE = MediaType.parse("image/*");
    public static final OkHttpClient client = new OkHttpClient();

    @Override
    public String create(InputStream input, String name, String realName, String contentType) throws IOException {

        RequestBody requestBody = new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("id", name)
            .addFormDataPart(
                "file",
                realName,
                RequestBody.create(MediaType.parse(contentType), IOUtils.toByteArray(input)))
            .build();

        Request request = new Request.Builder().url(contentRepoUrl).post(requestBody).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return Objects.requireNonNull(response.body()).string();
        }

    }

    @Override
    public Response get(String url) throws IOException {

        Request request = new Request.Builder().url(contentRepoUrl + url).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        return response;
    }

}
