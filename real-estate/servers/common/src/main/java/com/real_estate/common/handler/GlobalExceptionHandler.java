package com.real_estate.common.handler;

import com.real_estate.common.dtos.response.ResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {
	Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ResponseDto<Map<String, String>>> handleResponseStatusException(ResponseStatusException e) {
		Map<String, String> errors = new HashMap<>();
		errors.put("error", e.getStatusCode().toString());
		errors.put("message", e.getReason());
		return ResponseEntity.status(e.getStatusCode()).body(new ResponseDto<>(400, errors));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseDto<Map<String, String>>> handleException(Exception e) {
		e.printStackTrace();
		Map<String, String> errors = new HashMap<>();
		errors.put("message", e.getMessage());
		return ResponseEntity.status(500).body(new ResponseDto<>(400, errors));
	}
}


