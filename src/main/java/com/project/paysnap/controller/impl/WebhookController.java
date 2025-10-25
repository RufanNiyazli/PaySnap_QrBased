package com.project.paysnap.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.paysnap.entity.Order;
import com.project.paysnap.enums.OrderStatus;
import com.project.paysnap.repository.OrderRepository;
import com.project.paysnap.service.impl.EmailService;
import com.project.paysnap.service.impl.ReceiptService;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/webhook")
@Slf4j
public class WebhookController {

    @Value("${stripe.webhook-key}")
    private String WEBHOOK_SECRET;
    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;
    private final ReceiptService receiptService;
    private final EmailService emailService;

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, WEBHOOK_SECRET);

        } catch (Exception e) {
            log.info("Webhook verification failed {}", e.getMessage());
            return ResponseEntity.badRequest().body("Invalid signature");
        }
        log.info(" Received event: {}", event.getType());
        switch (event.getType()) {
            case "checkout.session.completed" -> handleCheckoutCompleted(event);
            case "checkout.session.expired" -> handleCheckoutExpired(event);
            default -> log.info("Unhandled event type: {}", event.getType());
        }

        return ResponseEntity.ok("Webhook processed");
    }

    private void handleCheckoutCompleted(Event event) {
        Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
        if (session == null) return;
        String sessionId = session.getId();
        Order order = orderRepository.findAll().stream().filter(o -> sessionId.equals(o.getStripeSessionId()))
                .findFirst().orElseThrow(() -> new RuntimeException("not find "));
        order.setOrderStatus(OrderStatus.PAID);
        order.setCompletedAt(LocalDateTime.now());
        log.info("Payment completed for order ID {}", order.getId());
        orderRepository.save(order);

        receiptService.generateReceipt(order).thenAccept(pdfPath -> {
            String subject = "Your Payment Receipt";
            String text = """
                    Hello %s,
                    
                    Thank you for your payment!
                    Your order #%s has been successfully processed.
                    Please find your payment receipt attached.
                    
                    — PaySnap Team
                    """.formatted(order.getUser().getUsername(), order.getId());
            emailService.sendReceiptEmail(order.getUser().getEmail(), subject, text, pdfPath);
        });

    }

    private void handleCheckoutExpired(Event event) {
        Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
        if (session == null) return;
        String sessionId = session.getId();
        Order order = orderRepository.findAll().stream().filter(o -> sessionId.equals(o.getStripeSessionId()))
                .findFirst().orElseThrow(() -> new RuntimeException("not find "));
        order.setOrderStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
        log.info("Payment expired for order ID {}", order.getId());
    }

}
