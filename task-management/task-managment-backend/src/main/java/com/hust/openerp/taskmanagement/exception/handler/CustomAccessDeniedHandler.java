package com.hust.openerp.taskmanagement.exception.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hust.openerp.taskmanagement.exception.ApiErrorResponse;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.nimbusds.jose.shaded.gson.Gson;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) throws IOException {
        log.error("AccessDeniedException: {}", ex.getMessage());

        ApiErrorResponse apiResponse = ApiErrorResponse.of(ErrorCode.ACCESS_DENIED);

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());

        new Gson().toJson(apiResponse, new TypeReference<ApiErrorResponse>() {
        }.getType(), response.getWriter());
    }
}
