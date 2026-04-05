package com.ecommerce.project.Payload;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long orderId;

    private String email;

    private List<OrderItemDTO> orderItems;

    private LocalDate orderDate;

    private PaymentDTO paymentDTO;

    private Double totalAmount;

    private String orderStatus;

    private Long addressId;

}
