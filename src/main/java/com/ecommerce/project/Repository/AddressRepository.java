package com.ecommerce.project.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.project.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long>{

    List<Address> findByUserUserId(Long userId);

}
