package com.ecommerce.project.Controller;
import com.ecommerce.project.Payload.CartDTO;
import com.ecommerce.project.Repository.CartRepository;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.service.CartService;
import com.ecommerce.project.util.AuthUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private CartRepository cartRepository;

    /**
     * Adds a product to the currently logged-in user's cart with the given quantity.
     *
     * @param productId product id to add into cart
     * @param quantity quantity to add for the product
     * @return updated cart details with CREATED status
     */
    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId, @PathVariable Integer quantity) {
        CartDTO cartDTO = cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.CREATED);
    }

    /**
     * Fetches all carts available in the system.
     *
     * @return list of cart DTOs
     */
    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getCarts() {
        List<CartDTO> cartDTOs = cartService.getAllCarts();
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
        Cart cart = cartRepository.findCartByEmail(emailId);
        Long cartId = cart.getCartId();
        CartDTO cartDTO = cartService.getCart(emailId, cartId);
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
        CartDTO cartDTO = cartService.updateProductQuantity(productId, operation.equalsIgnoreCase("delete") ? -1 : 1);
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
        String status =cartService.deleteProductFromCart(cartId, productId);
        return new ResponseEntity<String>(status, HttpStatus.OK);
    }

}
