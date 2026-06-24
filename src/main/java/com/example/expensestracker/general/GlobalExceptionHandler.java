package com.example.expensestracker.general;

import com.example.expensestracker.general.exception.AppRuntimeException;
import com.example.expensestracker.general.exception.ErrorCode;
import com.example.expensestracker.general.exception.ErrorStrategy;
import com.example.expensestracker.general.exception.error.ErrorResponseDTO;
import com.example.expensestracker.general.exception.error.ErrorResponseDescriptionListDTO;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private ErrorStrategy errorStrategy;


    /**
     * @param exception ConstraintViolationException
     * @return ResponseEntity<Object>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDescriptionListDTO> handleInvalidArgument(
            final MethodArgumentNotValidException exception) {
        log.error("MethodArgumentNotValidException in: {}", exception.getObjectName());

        List<String> errorList = new ArrayList<>();

        exception.getBindingResult().getFieldErrors().forEach(error -> {
            String errorMessage = String.format("error: field: %s, default message: %s, rejected value: %s",
                    error.getField(),
                    error.getDefaultMessage(),
                    error.getRejectedValue());

            log.error(errorMessage);

            errorList.add(errorMessage);

        });

        return new ResponseEntity<>(
                new ErrorResponseDescriptionListDTO(
                        ErrorCode.TEA001.getBusinessCode(),
                        errorStrategy.returnExceptionMessage(ErrorCode.TEA001.getBusinessMessage()),
                        errorStrategy.returnExceptionDescriptionList(errorList),
                        ErrorCode.TEA001.getHttpStatus()),
                HttpStatus.valueOf(ErrorCode.TEA001.getHttpStatus())
        );
    }

    @ExceptionHandler(AppRuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleAppRuntimeException(final AppRuntimeException exception) {
        if (exception.getCause() != null) {
            log.error("Application exception occurred", exception);
        } else {
            log.error("handleAppRuntimeException message: {}, description: {}",
                    exception.getMessage(),
                    exception.getDescription()
            );
        }
        return new ResponseEntity<>(
                new ErrorResponseDTO(
                        exception.getErrorCode().getBusinessCode(),
                        errorStrategy.returnExceptionMessage(exception.getMessage()),
                        errorStrategy.returnExceptionDescription(exception.getDescription()),
                        exception.getHttpStatusCode()),
                HttpStatus.valueOf(exception.getErrorCode().getHttpStatus()));
    }


    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleAppRuntimeException(final NoHandlerFoundException exception) {
        log.error("handleNoHandlerFoundException: message: {}, headers: {}, httpMethod: {}, requestUrl: {}",
                exception.getMessage(),
                exception.getHeaders(),
                exception.getHttpMethod(),
                exception.getRequestURL());

        return new ResponseEntity<>(
                new ErrorResponseDTO(
                        ErrorCode.TEA002.getBusinessCode(),
                        errorStrategy.returnExceptionMessage(ErrorCode.TEA002.getBusinessMessage()),
                        errorStrategy.returnExceptionDescription(
                                String.format("No handler for %s",
                                        exception.getRequestURL())),
                        ErrorCode.TEA002.getHttpStatus()),
                HttpStatus.valueOf(ErrorCode.TEA002.getHttpStatus()));
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolationException(
            final ConstraintViolationException e) {
        log.error("ConstraintViolationException: {}", e.getMessage());

        return new ResponseEntity<>(
                new ErrorResponseDTO(
                        ErrorCode.TEA001.getBusinessCode(),
                        errorStrategy.returnExceptionMessage(ErrorCode.TEA001.getBusinessMessage()),
                        errorStrategy.returnExceptionDescription(String.format("Throwable exception %s",
                                e.getMessage())),
                        ErrorCode.TEA001.getHttpStatus()),
                HttpStatus.valueOf(ErrorCode.TEA001.getHttpStatus())
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpMessageReadableException(
            final HttpMessageNotReadableException ex) {
        log.error("handleHttpMessageReadableException: {}", ex.getMessage());

        return new ResponseEntity<>(
                new ErrorResponseDTO(
                        ErrorCode.TEA001.getBusinessCode(),
                        errorStrategy.returnExceptionMessage(ErrorCode.TEA001.getBusinessMessage()),
                        errorStrategy.returnExceptionDescription(String.format("Throwable exception %s",
                                ex.getMessage())),
                        ErrorCode.TEA001.getHttpStatus()),
                HttpStatus.valueOf(ErrorCode.TEA001.getHttpStatus())
        );
    }

}