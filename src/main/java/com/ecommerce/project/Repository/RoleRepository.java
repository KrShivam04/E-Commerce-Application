package com.ecommerce.project.Repository;
import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.model.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {

    /**
     * Finds role by enum role name.
     */
    Optional<Role> findByRoleName(AppRole appRole);

}
