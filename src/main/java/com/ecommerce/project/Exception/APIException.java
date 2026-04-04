package com.ecommerce.project.Exception;

public class APIException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates empty API exception.
     */
    public APIException () {

    }

    /**
     * Creates API exception with custom message.
     */
    public APIException(String message) {
        super(message);
    }

}
