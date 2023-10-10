package com.vault.load.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ResponseEntityUtil<T> {

    public ResponseEntity createErrorResponse(final String code, final String description, final HttpStatus status) {
        ErrorResponse error = ErrorResponse.builder().code(code).description(description).build();
        return new ResponseEntity<>(new Result<>(error), status);
    }

    public ResponseEntity createErrorResponseList(final String code, final List<String> descriptions, final HttpStatus status) {
        Result<Object> result = new Result<>();
        descriptions.stream().forEach(description -> {
            ErrorResponse error = ErrorResponse.builder().code(code).description(description).build();
            result.addError(error);
        });
        return new ResponseEntity<>(result, status);
    }

    public ResponseEntity createSuccessResponse(final T data) {
        return new ResponseEntity<>(new Result<>(data), HttpStatus.OK);
    }
}
