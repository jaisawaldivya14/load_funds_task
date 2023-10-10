package com.vault.load.config;

import com.vault.load.utils.ErrorCodes;
import com.vault.load.utils.ResponseEntityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

@ControllerAdvice
@Slf4j
public class LoadFundsTaskExceptionHandler extends ResponseEntityExceptionHandler {

    protected final ResponseEntityUtil<?> responseEntityUtil = new ResponseEntityUtil<>();

    @ExceptionHandler(IOException.class)
    protected ResponseEntity<?> handleCustomerNotFoundException(IOException ex, WebRequest request) {
        log.error("IO exception: " + request.getDescription(false), ex);
        return responseEntityUtil.createErrorResponse(
                ErrorCodes.ERROR_CODE_SOMETHING_WENT_WRONG,
                ex.getMessage(),
                HttpStatus.BAD_GATEWAY
        );
    }
}
