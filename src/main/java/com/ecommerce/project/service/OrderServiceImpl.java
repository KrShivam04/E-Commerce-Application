package com.ecommerce.project.service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ecommerce.project.Exception.APIException;
import com.ecommerce.project.Exception.ResourceNotFoundException;
import com.ecommerce.project.Payload.OrderDTO;
import com.ecommerce.project.Payload.OrderItemDTO;
import com.ecommerce.project.Repository.AddressRepository;
import com.ecommerce.project.Repository.CartRepository;
import com.ecommerce.project.Repository.OrderItemRepository;
import com.ecommerce.project.Repository.OrderRepository;
import com.ecommerce.project.Repository.PaymentRepository;
import com.ecommerce.project.Repository.ProductRepository;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Order;
import com.ecommerce.project.model.OrderItem;
import com.ecommerce.project.model.Payment;
import com.ecommerce.project.model.Product;
import jakarta.transaction.Transactional;


@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PaymentRepository PaymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDTO placeOrder(
        String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage
    ) {
        logger.info("Placing order email={}, addressId={}, paymentMethod={}, pgName={}, pgStatus={}", emailId, addressId, paymentMethod, pgName, pgStatus);
        // getting user cart

        Cart cart = cartRepository.findCartByEmail(emailId);
        if (cart == null) {
            logger.warn("Cannot place order because cart was not found email={}", emailId);
            throw new ResourceNotFoundException("Cart", "email", emailId);
        }
        Address address = addressRepository.findById(addressId).orElseThrow(()-> new ResourceNotFoundException("Address", "addressId", addressId));

        // creating new order with payment info

        Order order = new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted");
        order.setAddress(address);

        Payment payment = new Payment(paymentMethod, pgPaymentId, pgStatus, pgResponseMessage, pgName);
        payment.setOrder(order);
        payment = PaymentRepository.save(payment);
        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);

        // Get the items from the cart into the order item

        List<CartItem> cartItems = cart.getCartItem();
        if (cartItems.isEmpty()) {
            logger.warn("Cannot place order because cart is empty cartId={}, email={}", cart.getCartId(), emailId);
            throw new APIException("Cart is empty.");
        }
        logger.debug("Creating order items from cart cartId={}, itemCount={}", cart.getCartId(), cartItems.size());
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem: cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);
            orderItems.add(orderItem);
        }
        
        orderItems =  orderItemRepository.saveAll(orderItems);

        // update Product Stock

        new ArrayList<>(cart.getCartItem()).forEach(item -> {
            int quantity = item.getQuantity();
            Product product = item.getProduct();
            logger.debug("Updating stock for productId={}, orderedQuantity={}", product.getProductId(), quantity);
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);

            // clearing the cart
            cartService.deleteProductFromCart(cart.getCartId(), item.getProduct().getProductId());
        });

        // sending back the order summary 

        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderItems.forEach(item-> 
            orderDTO.getOrderItems().add(
                modelMapper.map(item, OrderItemDTO.class)
            )
        );
        orderDTO.setAddressId(addressId);
        
        // Map payment to paymentDTO
        if (savedOrder.getPayment() != null) {
            orderDTO.setPaymentDTO(modelMapper.map(savedOrder.getPayment(), com.ecommerce.project.Payload.PaymentDTO.class));
        }

        logger.info("Order placed orderId={}, email={}, itemCount={}, totalAmount={}", savedOrder.getOrderId(), emailId, orderItems.size(), savedOrder.getTotalAmount());
        return orderDTO;
    }

}
