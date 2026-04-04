package com.ecommerce.project.service;

import java.util.List;

import com.ecommerce.project.Payload.CartDTO;

import jakarta.transaction.Transactional;

public interface CartService {
    /**
     * Adds a product with quantity to the current user's cart.
     */
    CartDTO addProductToCart(Long productId, Integer quantity);

    /**
     * Returns all carts.
     */
    List<CartDTO> getAllCarts();

    /**
     * Returns a cart by user email and cart id.
     */
    CartDTO getCart(String emailId, Long cartId);

    /**
     * Updates quantity of a product in the current user's cart.
     */
    @Transactional
    CartDTO updateProductQuantity(Long productId, Integer quantity);

    /**
     * Deletes a product from a cart.
     */
    String deleteProductFromCart(Long cartId, Long productId);

    /**
     * Recalculates a product price entry inside existing carts.
     */
    void updateProductInCarts(Long cartId, Long productId);
}
