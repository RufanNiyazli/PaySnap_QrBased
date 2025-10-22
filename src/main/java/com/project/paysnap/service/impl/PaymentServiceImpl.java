package com.project.paysnap.service.impl;

import com.project.paysnap.entity.Order;
import com.project.paysnap.entity.User;
import com.project.paysnap.enums.OrderStatus;
import com.project.paysnap.repository.OrderRepository;
import com.project.paysnap.service.IPaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {
    private final OrderRepository orderRepository;

    @Value("${stripe.cancel-url}")
    private String CANCEL_URL;

    @Value("${stripe.success-url}")
    private String SUCCESS_URL;

    @Override

    public Order createOrder(User user, String description, Long amount, String currency) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(SUCCESS_URL)
                .setCancelUrl(CANCEL_URL)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(currency)
                                                .setUnitAmount(amount)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(description)
                                                                .build()
                                                )
                                                .build()
                                ).build()

                )
                .build();

        Session session = Session.create(params);
        Order order = Order.builder()
                .createdAt(LocalDateTime.now())
                .amount(amount)
                .orderStatus(OrderStatus.PENDING)
                .currency(currency)
                .description(description)
                .stripeSessionId(session.getId())
                .paymentUrl(session.getUrl())
                .user(user)
                .build();
        orderRepository.save(order);
        return order;
    }
}
