package com.ecommerce.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.project.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long>{

    

}
