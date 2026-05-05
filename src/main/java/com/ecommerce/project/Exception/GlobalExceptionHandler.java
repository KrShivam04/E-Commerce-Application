package com.ecommerce.project.Exception;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.ecommerce.project.Payload.APIResponse;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

// Global Exception Handler Class
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles validation errors and returns field-wise messages.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> myMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        Map<String, String> response = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(err-> {
            String fieldName = ((FieldError)err).getField();
            String fieldMsg = err.getDefaultMessage();
            response.put(fieldName, fieldMsg);
        });
        logger.warn("Validation failed: {}", response);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    /**
     * Handles resource not found exceptions.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> myResourceNotFoundException(ResourceNotFoundException e) {
        String message = e.getMessage();
        logger.warn("Resource not found: {}", message);
        APIResponse apiResponse = new APIResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles custom API exceptions for bad requests.
     */
    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> myAPIException(APIException e) {
        String message = e.getMessage();
        logger.warn("API exception: {}", message);
        APIResponse apiResponse = new APIResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Logs unexpected exceptions so production failures can be investigated.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse> myGlobalException(Exception e) {
        logger.error("Unexpected application error", e);
        APIResponse apiResponse = new APIResponse("Internal server error", false);
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }



}
