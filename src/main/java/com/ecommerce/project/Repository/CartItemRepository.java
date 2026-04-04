package com.ecommerce.project.Repository;
import com.ecommerce.project.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface CartItemRepository extends JpaRepository<CartItem,Long> {

    /**
     * Finds cart item by cart id and product id.
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = ?1 AND ci.product.id = ?2")
    CartItem findCartItemByProductIdAndCartId(Long cartId, Long productId);

    /**
     * Deletes cart item by cart id and product id.
     */
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.cart.id = ?1 AND c.product.id = ?2")
    void deleteCartItemByProductIdAndCartId(Long cartId, Long productId);

}
