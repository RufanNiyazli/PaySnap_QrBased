package com.project.paysnap.service.impl;


import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.project.paysnap.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReceiptService {
    @Async
    public CompletableFuture<String> generateReceipt(Order order) {
        String path = System.getProperty("java.io.tmpdir") + "//receipt_" + order.getId() + ".pdf";
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);

            document.add(new Paragraph("PAYMENT RECEIPT", titleFont));
            document.add(new Paragraph("Order ID: " + order.getId(), normalFont));
            document.add(new Paragraph("Customer: " + order.getUser().getUsername(), normalFont));
            document.add(new Paragraph("Email: " + order.getUser().getEmail(), normalFont));
            document.add(new Paragraph("Amount: " + order.getAmount() + " " + order.getCurrency(), normalFont));
            document.add(new Paragraph("Status: " + order.getOrderStatus(), normalFont));
            document.add(new Paragraph("Completed At: " +
                    order.getCompletedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), normalFont));

            document.add(new Paragraph("\nThank you for your payment!", normalFont));
            document.close();
            log.info("Pdf created successfully at {}", path);
            return CompletableFuture.completedFuture(path);

        } catch (Exception e) {
            log.error("Error creating pdf receipt {}", e.getMessage());
            return CompletableFuture.failedFuture(e);
        }


    }
}
