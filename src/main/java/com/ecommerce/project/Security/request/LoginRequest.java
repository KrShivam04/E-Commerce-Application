package com.ecommerce.project.Security.request;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    /**
     * Returns username provided in login request.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username for login request.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns password provided in login request.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password for login request.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
