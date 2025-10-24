package com.project.paysnap.controller.impl;


import com.google.zxing.WriterException;

import com.project.paysnap.entity.Order;
import com.project.paysnap.repository.OrderRepository;
import com.project.paysnap.service.impl.QrCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
public class QrController {

    private final OrderRepository orderRepository;
    private final QrCodeService qrCodeService;

    @GetMapping("/{orderId}")
    public ResponseEntity<byte[]> getQrCode(@PathVariable Long orderId) throws WriterException, IOException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getPaymentUrl() == null) {
            throw new RuntimeException("Payment URL not available");
        }

        String base64Qr = qrCodeService.createQrCode(order.getPaymentUrl(), 300, 300);
        byte[] imageBytes = Base64.getDecoder().decode(base64Qr);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=payment_qr.png")
                .contentType(MediaType.IMAGE_PNG)
                .body(imageBytes);
    }
}
