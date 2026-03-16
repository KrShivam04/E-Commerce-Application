package com.ecommerce.project.Exception;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ecommerce.project.Payload.APIResponse;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

// Global Exception Handler Class
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> myMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        Map<String, String> response = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(err-> {
            String fieldName = ((FieldError)err).getField();
            String fieldMsg = err.getDefaultMessage();
            response.put(fieldName, fieldMsg);
        });
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    // custom exception handler class
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> myResourceNotFoundException(ResourceNotFoundException e) {
        String message = e.getMessage();
        APIResponse apiResponse = new APIResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> myAPIException(APIException e) {
        String message = e.getMessage();
        APIResponse apiResponse = new APIResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }




}
