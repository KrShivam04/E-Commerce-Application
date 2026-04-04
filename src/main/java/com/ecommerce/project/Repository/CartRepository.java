package com.ecommerce.project.Repository;

import com.ecommerce.project.model.Cart;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartRepository extends JpaRepository<Cart,Long> {

    /**
     * Finds cart by user's email.
     */
    @Query("SELECT c FROM Cart c WHERE c.user.email = ?1")
    Cart findCartByEmail(String email);

    /**
     * Finds cart by user's email and cart id.
     */
    @Query("SELECT c FROM Cart c WHERE c.user.email = ?1 AND c.id = ?2")
    Cart findCartByEmailAndCartId(String emailId, Long cartId);

    /**
     * Returns carts containing the given product id.
     */
    @Query("SELECT c FROM Cart c JOIN FETCH c.cartItem ci JOIN FETCH ci.product p WHERE p.id = ?1")
    List<Cart> findCartsByProductId(Long productId);

}
