package com.project.paysnap.service.impl;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Async
    public void sendReceiptEmail(String to, String subject, String text, String path) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
            if (path != null) {
                FileSystemResource fileSystemResource = new FileSystemResource(new File(path));
                helper.addAttachment("receipt.pdf", fileSystemResource);

            }
            mailSender.send(message);
            log.info("Receipt email sent to {}", to);
        } catch (Exception e) {
            log.error(" Error sending receipt email: {}", e.getMessage());
        }
    }

}
