package com.ecommerce.project.Security.services;
import com.ecommerce.project.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

    private long  id;
    private String username;
    private String email;
    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Creates a security principal with identity and authority information.
     */
    public UserDetailsImpl(long id, String username, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    /**
     * Builds security principal from application user entity.
     */
    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getRoleName().name())).collect(Collectors.toList());
        return new UserDetailsImpl(
                user.getUserId(), user.getUserName(), user.getEmail(), user.getPassword(), authorities
        );
    }

    /**
     * Returns authorities assigned to current authenticated user.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Returns encoded password.
     */
    @Override
    public @Nullable String getPassword() {
        return password;
    }

    /**
     * Returns login username.
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Indicates whether account is not expired.
     */
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    /**
     * Indicates whether account is not locked.
     */
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    /**
     * Indicates whether credentials are not expired.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    /**
     * Indicates whether user is enabled.
     */
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    /**
     * Compares principals based on user id.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }

}
