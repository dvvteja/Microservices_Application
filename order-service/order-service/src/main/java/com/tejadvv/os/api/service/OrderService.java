package com.tejadvv.os.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tejadvv.os.api.common.Payment;
import com.tejadvv.os.api.common.TransactionRequest;
import com.tejadvv.os.api.common.TransactionResponse;
import com.tejadvv.os.api.entity.Order;
import com.tejadvv.os.api.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RefreshScope
public class OrderService {

    @Autowired
    private OrderRepository repository;

    @Autowired
    @Lazy
    private RestTemplate template;

    @Value("${microservice.payment-service.endpoints.endpoint.uri}")
    private String ENDPOINT_URL;

    Logger logger= LoggerFactory.getLogger(OrderService.class);

    public TransactionResponse saveOrder(TransactionRequest request) throws JsonProcessingException {
        String response = "";
        Order order = request.getOrder();
        Payment payment = request.getPayment();
        payment.setOrderId(order.getId());
        payment.setAmount(order.getPrice());
        logger.info("OrderService request:{}", new ObjectMapper().writeValueAsString(request));
        Payment paymentResponse = template.postForObject(ENDPOINT_URL, payment, Payment.class);
        logger.info("OrderService getting Response from PaymentService :{}", new ObjectMapper().writeValueAsString(paymentResponse));
        response = paymentResponse.getPaymentStatus().equals("success") ? "payment processing successful and order placed" : "there is a failure in payment api , order added to cart";
        repository.save(order);
        return new TransactionResponse(order, paymentResponse.getAmount(), paymentResponse.getTransactionId(), response);
    }

}
