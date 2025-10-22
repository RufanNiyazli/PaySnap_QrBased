package com.project.paysnap.controller;

import com.project.paysnap.entity.Order;
import com.project.paysnap.entity.User;
import com.stripe.exception.StripeException;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface IOrderController {
    public Map<String, Object> createOrder(String authHeader, @RequestBody Map<String, String> body) throws StripeException;

}
