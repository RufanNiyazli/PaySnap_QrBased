package com.project.paysnap.controller.impl;

import com.project.paysnap.controller.IOrderController;
import com.project.paysnap.entity.Order;
import com.project.paysnap.entity.User;
import com.project.paysnap.repository.UserRepository;
import com.project.paysnap.security.JwtService;
import com.project.paysnap.service.IPaymentService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class OrderControllerImpl implements IOrderController {
    private final IPaymentService paymentService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    @PostMapping("/api/orders/create")
    public Map<String, Object> createOrder(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> body) throws StripeException {
        String token = authHeader.substring(7);
        String email = jwtService.getEmailByToken(token);
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new RuntimeException("This user not exist!"));
        String description = body.get("description");
        Long amount = Long.parseLong(body.get("amount")); // sent-lə
        String currency = body.getOrDefault("currency", "usd");
        Order order = paymentService.createOrder(user, description, amount, currency);

        return Map.of(
                "orderId", order.getId(),
                "paymentUrl", order.getPaymentUrl(),
                "status", order.getOrderStatus()
        );


    }
}
