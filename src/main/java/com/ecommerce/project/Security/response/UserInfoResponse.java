package com.ecommerce.project.Security.response;
import java.util.List;

public class UserInfoResponse {

    private Long id;
    private String jwtToken;
    private String username;
    private List<String> roles;

    /**
     * Creates user info response including jwt token.
     */
    public UserInfoResponse(Long id, String username, List<String> roles, String jwtToken) {
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.jwtToken = jwtToken;
    }

    /**
     * Creates user info response without jwt token.
     */
    public UserInfoResponse(long id2, String username2, List<String> roles2) {
        this.id = id2;
        this.username = username2;
        this.roles = roles2;
    }

    /**
     * Returns user id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets user id.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns jwt token.
     */
    public String getJwtToken() {
        return jwtToken;
    }

    /**
     * Sets jwt token.
     */
    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    /**
     * Returns username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns roles assigned to user.
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * Sets user roles.
     */
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

}
