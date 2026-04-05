package com.ecommerce.project.service;

import com.ecommerce.project.Payload.OrderDTO;

public interface OrderService {

    OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId,
            String pgStatus, String pgResponseMessage);

}
