package com.chinuthon.project.uber.uber.advices;

import com.chinuthon.project.uber.uber.exceptions.RuntimeConflictException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice   // Handles all controllers errors & response handling logic
public class GlobalExceptionHandler {

    // To avoid code repetition, we can create a helper method to build the ResponseEntity for all exceptions
    private ResponseEntity<ApiResponse<?>> buildResponseEntity(ApiError ApiError) {
        return new ResponseEntity<>(new ApiResponse<>(ApiError),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<?>> handleNoSuchResourceException(NoSuchElementException exception) {

        ApiError ApiError = new ApiError();
        ApiError.setStatus(HttpStatus.NOT_FOUND);
        ApiError.setMessage(exception.getMessage());

        return buildResponseEntity(ApiError);
    }

    @ExceptionHandler(RuntimeConflictException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntimeConflictException(RuntimeException exception) {

        ApiError ApiError = new ApiError();
        ApiError.setStatus(HttpStatus.CONFLICT);
        ApiError.setMessage(exception.getMessage());

        return buildResponseEntity(ApiError);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleInternalServerException(Exception exception) {
        ApiError ApiError = new ApiError();
        ApiError.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        ApiError.setMessage(exception.getMessage());

        return buildResponseEntity(ApiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleInputValidationException(MethodArgumentNotValidException exception) {
         List<String> errors =exception
                .getBindingResult()
                .getAllErrors()
                .stream()
                .map(objectError ->
                        objectError.getDefaultMessage())
                .collect(Collectors.toList());

        ApiError ApiError = new ApiError();
        ApiError.setStatus(HttpStatus.BAD_REQUEST);
        ApiError.setMessage(errors.toString());

        return buildResponseEntity(ApiError);
    }
}