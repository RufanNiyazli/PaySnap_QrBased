package com.project.paysnap.service;

import com.project.paysnap.entity.Order;
import com.project.paysnap.entity.User;
import com.stripe.exception.StripeException;

public interface IPaymentService {

    public Order createOrder(User user, String description, Long amount, String currency) throws StripeException;
}
