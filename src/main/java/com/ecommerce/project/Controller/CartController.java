package com.ecommerce.project.Controller;
import com.ecommerce.project.Payload.CartDTO;
import com.ecommerce.project.Payload.CartItemDTO;
import com.ecommerce.project.Repository.CartRepository;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.service.CartService;
import com.ecommerce.project.util.AuthUtil;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private CartRepository cartRepository;

    @PostMapping("/carts/create")
    public ResponseEntity<String> createOrUpdateCart(@RequestBody List<CartItemDTO> cartItems) {
        logger.info("Creating or updating cart with {} items", cartItems.size());
        String response = cartService.createOrUpdateCartWithItems(cartItems);
        logger.info("Cart created or updated successfully with {} items", cartItems.size());
        return new ResponseEntity<String>(response, HttpStatus.CREATED);
    }


    /**
     * Adds a product to the currently logged-in user's cart with the given quantity.
     *
     * @param productId product id to add into cart
     * @param quantity quantity to add for the product
     * @return updated cart details with CREATED status
     */
    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId, @PathVariable Integer quantity) {
        logger.info("Adding product to cart productId={}, quantity={}", productId, quantity);
        CartDTO cartDTO = cartService.addProductToCart(productId, quantity);
        logger.info("Product added to cart productId={}, quantity={}", productId, quantity);
        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.CREATED);
    }

    /**
     * Fetches all carts available in the system.
     *
     * @return list of cart DTOs
     */
    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getCarts() {
        logger.debug("Fetching all carts");
        List<CartDTO> cartDTOs = cartService.getAllCarts();
        logger.debug("Fetched {} carts", cartDTOs.size());
        return new ResponseEntity<List<CartDTO>>(cartDTOs, HttpStatus.FOUND);
    }

    /**
     * Fetches the cart of the currently authenticated user.
     *
     * @return current user's cart details
     */
    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCartById() {
        String emailId = authUtil.loggedInEmail();
        logger.debug("Fetching cart for current user email={}", emailId);
        Cart cart = cartRepository.findCartByEmail(emailId);
        Long cartId = cart.getCartId();
        CartDTO cartDTO = cartService.getCart(emailId, cartId);
        logger.debug("Fetched cart cartId={} for email={}", cartId, emailId);
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    /**
     * Updates quantity for a product in cart based on operation value.
     * If operation is "delete", quantity is reduced by 1; otherwise increased by 1.
     *
     * @param productId product id in cart
     * @param operation operation name (for example: add/delete)
     * @return updated cart details
     */
    @PutMapping("/cart/product/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Long productId,@PathVariable String operation) {
        logger.info("Updating cart product quantity productId={}, operation={}", productId, operation);
        CartDTO cartDTO = cartService.updateProductQuantity(productId, operation.equalsIgnoreCase("delete") ? -1 : 1);
        logger.info("Updated cart product quantity productId={}, operation={}", productId, operation);
        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
    }

    /**
     * Removes a specific product from the specified cart.
     *
     * @param cartId cart id from which product needs to be removed
     * @param productId product id to remove
     * @return deletion status message
     */
    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId,@PathVariable Long productId) {
        logger.info("Deleting product from cart cartId={}, productId={}", cartId, productId);
        String status =cartService.deleteProductFromCart(cartId, productId);
        logger.info("Deleted product from cart cartId={}, productId={}", cartId, productId);
        return new ResponseEntity<String>(status, HttpStatus.OK);
    }

}
