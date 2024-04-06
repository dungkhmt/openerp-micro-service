package com.example.shared.utils;

import com.example.shared.response.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    public static ResponseEntity<CommonResponse<Object>> toSuccessCommonResponse(Object data) {
        return new ResponseEntity<>(new CommonResponse<>(data), HttpStatus.OK);
    }

    public static ResponseEntity<CommonResponse<Object>> toInternalErrorCommonResponse() {
        return new ResponseEntity<>(CommonResponse.internalError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
