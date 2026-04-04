package com.ecommerce.project.Security.response;
import lombok.Data;

@Data
public class MessageResponse {

    private String message;

    /**
     * Creates message response payload.
     */
    public MessageResponse(String message) {
        this.message = message;
    }

}
