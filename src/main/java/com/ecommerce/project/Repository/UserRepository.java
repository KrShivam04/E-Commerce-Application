package com.ecommerce.project.Repository;
import com.ecommerce.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds user by username.
     */
    Optional<User> findByUserName(String username);

    /**
     * Checks if username already exists.
     */
    Boolean existsByUserName(String username);

    /**
     * Checks if email already exists.
     */
    Boolean existsByEmail(String email);
}
