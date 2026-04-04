package com.ecommerce.project.service;
import com.ecommerce.project.Exception.APIException;
import com.ecommerce.project.Exception.ResourceNotFoundException;
import com.ecommerce.project.Payload.CartDTO;
import com.ecommerce.project.Payload.ProductDTO;
import com.ecommerce.project.Repository.CartItemRepository;
import com.ecommerce.project.Repository.CartRepository;
import com.ecommerce.project.Repository.ProductRepository;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.util.AuthUtil;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Adds a product with quantity into the authenticated user's cart.
     */
    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        // finding existing cart or create one
        Cart cart = createCart();

        // retrieve the product detail 
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // performing validation if the product exists in the user cart or not 
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);

        if (cartItem != null ){
            throw new APIException("Product "+ product.getProductName()+ " already exists in the cart.");
        }
        // if the stock is not available;
        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");
        }

        // if stock is less than requested quantity
        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName() + " less than or equal to the quantity " + product.getQuantity());
        }
        
        // creating cart item
        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        // saving the cart item
        cartItemRepository.save(newCartItem);

        product.setQuantity(product.getQuantity());

        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));

        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItem();

        //returing updated cart 
        Stream<ProductDTO> productDTOstream = cartItems.stream().map(item -> {
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setQuantity(item.getQuantity());
            return map;
        });

        cartDTO.setProducts(productDTOstream.toList());

        return cartDTO;

    }

    /**
     * Returns existing cart for current user, or creates a new one.
     */
    private Cart createCart() {

        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());

        if (userCart != null) {
            return userCart;
        }

        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());
        Cart newCart = cartRepository.save(cart);
        return newCart;

    }

    /**
     * Fetches all carts with associated products.
     */
    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if(carts.size() == 0) {
            throw new APIException("No Items in cart exists.");
        }
        List<CartDTO> cartDTOs = carts.stream().map(cart-> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            List<ProductDTO> product = cart.getCartItem().stream().map(p-> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());
            cartDTO.setProducts(product);
            return cartDTO;
        }).collect(Collectors.toList());

        return cartDTOs;
    }

    /**
     * Fetches a specific cart by email and cart id.
     */
    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        cart.getCartItem().forEach(
            c->c.getProduct().setQuantity(c.getQuantity())
        );
        List<ProductDTO> products = cart.getCartItem().stream().map(p-> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());
        cartDTO.setProducts(products);
        return cartDTO;
    }

    /**
     * Increments or decrements quantity for a product in current user's cart.
     */
    @Transactional
    @Override
    public CartDTO updateProductQuantity(Long productId, Integer quantity) {
        // getting the cart of the loggedin user
        String emailId = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(emailId);
        Long cartId = userCart.getCartId();
        Cart cart = cartRepository.findById(cartId).orElseThrow(()-> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // if the stock is not available;
        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");
        }

        // if stock is less than requested quantity
        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName() + " less than or equal to the quantity " + product.getQuantity());
        }
        
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not available in the cart.");
        }

        int newQuantity = cartItem.getQuantity() + quantity;
        if (newQuantity < 0 ) {
            throw new APIException("The resulting quantity cannot be negative!");
        }

        if (newQuantity == 0 ){
            deleteProductFromCart(cartId, productId);
        } else { 
            // updating the item
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
            cartRepository.save(cart);
        }

        CartItem updatedItem = cartItemRepository.save(cartItem);

        if (updatedItem.getQuantity() == 0) {
            cartItemRepository.deleteById(updatedItem.getCartItemId());
        }

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItem();

        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO prd = modelMapper.map(item.getProduct(), ProductDTO.class);
            prd.setQuantity(item.getQuantity());
            return prd;
        });

        cartDTO.setProducts(productStream.toList());

        return cartDTO;
    }

    /**
     * Removes a product from a cart and adjusts total price.
     */
    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {

        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartItem == null ){
            throw new ResourceNotFoundException("Product", "productId", productId);
        }

        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice()* cartItem.getQuantity()));

        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);

        return "Product " + cartItem.getProduct().getProductName() + " removed from the cart !!!";

    }

    /**
     * Refreshes cart item price when product price changes.
     */
    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(()-> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not avaiable in the cart!!!");
        } 

        Double cartPrice = cart.getTotalPrice( ) - (cartItem.getProductPrice() * cartItem.getQuantity());
        cartItem.setProductPrice(product.getSpecialPrice());
        cart.setTotalPrice(cartPrice);

        cartItem = cartItemRepository.save(cartItem);

    }

}
