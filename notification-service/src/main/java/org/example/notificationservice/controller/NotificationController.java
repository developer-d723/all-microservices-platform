package org.example.notificationservice.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.notificationservice.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/email")
    public ResponseEntity<Void> sendEmail(@RequestBody DirectEmailRequest request) {
        emailService.sendDirectEmail(request.getTo(), request.getSubject(), request.getText());
        return ResponseEntity.ok().build();
    }

    @Data
    static class DirectEmailRequest {
        private String to;
        private String subject;
        private String text;
    }
}