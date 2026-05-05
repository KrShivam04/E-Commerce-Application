package com.ecommerce.project.service;
import com.ecommerce.project.Exception.APIException;
import com.ecommerce.project.Exception.ResourceNotFoundException;
import com.ecommerce.project.Payload.CartDTO;
import com.ecommerce.project.Payload.CartItemDTO;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

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
        logger.info("Adding product to cart productId={}, quantity={}", productId, quantity);
        // finding existing cart or create one
        Cart cart = createCart();
        logger.debug("Using cart cartId={} while adding productId={}", cart.getCartId(), productId);

        // retrieve the product detail 
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // performing validation if the product exists in the user cart or not 
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);

        if (cartItem != null ){
            logger.warn("Product already exists in cart cartId={}, productId={}", cart.getCartId(), productId);
            throw new APIException("Product "+ product.getProductName()+ " already exists in the cart.");
        }
        // if the stock is not available;
        if (product.getQuantity() == 0) {
            logger.warn("Product is out of stock productId={}", productId);
            throw new APIException(product.getProductName() + " is not available");
        }

        // if stock is less than requested quantity
        if (product.getQuantity() < quantity) {
            logger.warn("Requested quantity exceeds stock productId={}, requestedQuantity={}, availableQuantity={}", productId, quantity, product.getQuantity());
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

        logger.info("Product added to cart cartId={}, productId={}, quantity={}", cart.getCartId(), productId, quantity);
        return cartDTO;

    }

    /**
     * Returns existing cart for current user, or creates a new one.
     */
    private Cart createCart() {

        logger.debug("Finding cart for current user");
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());

        if (userCart != null) {
            logger.debug("Found existing cart cartId={}", userCart.getCartId());
            return userCart;
        }

        logger.info("Creating cart for current user");
        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());
        Cart newCart = cartRepository.save(cart);
        logger.info("Created cart cartId={}", newCart.getCartId());
        return newCart;
        
    }

    /**
     * Fetches all carts with associated products.
     */
    @Override
    public List<CartDTO> getAllCarts() {
        logger.debug("Fetching all carts");
        List<Cart> carts = cartRepository.findAll();
        if(carts.size() == 0) {
            logger.warn("No carts found");
            throw new APIException("No Items in cart exists.");
        }
        List<CartDTO> cartDTOs = carts.stream().map(cart-> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

           List<ProductDTO> product = cart.getCartItem().stream().map(cartItem -> {
                ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                productDTO.setQuantity(cartItem.getQuantity()); // Set the quantity from CartItem
                return productDTO;
            }).collect(Collectors.toList());

            
            cartDTO.setProducts(product);
            return cartDTO;
        }).collect(Collectors.toList());

        logger.debug("Fetched {} carts", cartDTOs.size());
        return cartDTOs;
    }

    /**
     * Fetches a specific cart by email and cart id.
     */
    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        logger.debug("Fetching cart cartId={} for email={}", cartId, emailId);
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
        if (cart == null) {
            logger.warn("Cart not found cartId={} for email={}", cartId, emailId);
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        cart.getCartItem().forEach(
            c->c.getProduct().setQuantity(c.getQuantity())
        );
        List<ProductDTO> products = cart.getCartItem().stream().map(p-> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());
        cartDTO.setProducts(products);
        logger.debug("Fetched cart cartId={} with {} products", cartId, products.size());
        return cartDTO;
    }

    /**
     * Increments or decrements quantity for a product in current user's cart.
     */
    @Transactional
    @Override
    public CartDTO updateProductQuantity(Long productId, Integer quantity) {
        logger.info("Updating product quantity in cart productId={}, quantityDelta={}", productId, quantity);
        // getting the cart of the loggedin user
        String emailId = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(emailId);
        Long cartId = userCart.getCartId();
        Cart cart = cartRepository.findById(cartId).orElseThrow(()-> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // if the stock is not available;
        if (product.getQuantity() == 0) {
            logger.warn("Cannot update cart because product is out of stock productId={}", productId);
            throw new APIException(product.getProductName() + " is not available");
        }

        // if stock is less than requested quantity
        if (product.getQuantity() < quantity) {
            logger.warn("Cannot update cart because requested delta exceeds stock productId={}, quantityDelta={}, availableQuantity={}", productId, quantity, product.getQuantity());
            throw new APIException("Please, make an order of the " + product.getProductName() + " less than or equal to the quantity " + product.getQuantity());
        }
        
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if (cartItem == null) {
            logger.warn("Cannot update cart because product is missing cartId={}, productId={}", cartId, productId);
            throw new APIException("Product " + product.getProductName() + " not available in the cart.");
        }

        int newQuantity = cartItem.getQuantity() + quantity;
        if (newQuantity < 0 ) {
            logger.warn("Cannot update cart because resulting quantity is negative cartId={}, productId={}, newQuantity={}", cartId, productId, newQuantity);
            throw new APIException("The resulting quantity cannot be negative!");
        }

        if (newQuantity == 0 ){
            logger.info("Quantity became zero; removing product from cart cartId={}, productId={}", cartId, productId);
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

        logger.info("Updated product quantity in cart cartId={}, productId={}, newQuantity={}", cartId, productId, newQuantity);
        return cartDTO;
    }

    /**
     * Removes a product from a cart and adjusts total price.
     */
    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        logger.info("Deleting product from cart cartId={}, productId={}", cartId, productId);

        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartItem == null ){
            logger.warn("Cannot delete product from cart because item was not found cartId={}, productId={}", cartId, productId);
            throw new ResourceNotFoundException("Product", "productId", productId);
        }

        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice()* cartItem.getQuantity()));

        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);

        logger.info("Deleted product from cart cartId={}, productId={}", cartId, productId);
        return "Product " + cartItem.getProduct().getProductName() + " removed from the cart !!!";

    }

    /**
     * Refreshes cart item price when product price changes.
     */
    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        logger.info("Refreshing product price in cart cartId={}, productId={}", cartId, productId);
        Cart cart = cartRepository.findById(cartId).orElseThrow(()-> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if (cartItem == null) {
            logger.warn("Cannot refresh product in cart because item was not found cartId={}, productId={}", cartId, productId);
            throw new APIException("Product " + product.getProductName() + " not avaiable in the cart!!!");
        } 

        Double cartPrice = cart.getTotalPrice( ) - (cartItem.getProductPrice() * cartItem.getQuantity());
        cartItem.setProductPrice(product.getSpecialPrice());
        cart.setTotalPrice(cartPrice);

        cartItem = cartItemRepository.save(cartItem);
        logger.info("Refreshed product price in cart cartId={}, productId={}", cartId, productId);

    }

    @Transactional
    @Override
    public String createOrUpdateCartWithItems(List<CartItemDTO> cartItems) {
        logger.info("Creating or updating cart with {} requested items", cartItems.size());
        // Get user's email
        String emailId = authUtil.loggedInEmail();

        // Check if an existing cart is available or create a new one
        Cart existingCart = cartRepository.findCartByEmail(emailId);
        if (existingCart == null) {
            logger.info("Creating new cart for email={}", emailId);
            existingCart = new Cart();
            existingCart.setTotalPrice(0.00);
            existingCart.setUser(authUtil.loggedInUser());
            existingCart = cartRepository.save(existingCart);
        } else {
            // Clear all current items in the existing cart
            logger.info("Clearing existing cart cartId={} for email={}", existingCart.getCartId(), emailId);
            cartItemRepository.deleteAllByCartId(existingCart.getCartId());
        }

        double totalPrice = 0.00;

        // Process each item in the request to add to the cart
        for (CartItemDTO cartItemDTO : cartItems) {
            Long productId = cartItemDTO.getProductId();
            Integer quantity = cartItemDTO.getQuantity();
            logger.debug("Adding requested cart item productId={}, quantity={}", productId, quantity);

            // Find the product by ID
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

            // Directly update product stock and total price
            // product.setQuantity(product.getQuantity() - quantity);
            totalPrice += product.getSpecialPrice() * quantity;

            // Create and save cart item
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(existingCart);
            cartItem.setQuantity(quantity);
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setDiscount(product.getDiscount());
            cartItemRepository.save(cartItem);
        }

        // Update the cart's total price and save
        existingCart.setTotalPrice(totalPrice);
        cartRepository.save(existingCart);
        logger.info("Cart created or updated cartId={}, itemCount={}, totalPrice={}", existingCart.getCartId(), cartItems.size(), totalPrice);
        return "Cart created/updated with the new items successfully";
    }


}
