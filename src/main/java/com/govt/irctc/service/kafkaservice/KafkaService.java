package com.govt.irctc.service.kafkaservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.govt.irctc.dto.SendEmailDto;
import com.govt.irctc.service.emailservice.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {
    private final EmailService emailService;

    @Autowired
    public KafkaService(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "email-topic", groupId = "emailService", containerFactory = "kafkaListenerContainerFactory")
    public void emailQueue(SendEmailDto emailDto) throws JsonProcessingException {
        String from = emailDto.getFrom();
        String to = emailDto.getTo();
        String subject = emailDto.getSubject();
        String body = emailDto.getBody();

        emailService.sendEmail(from, to, subject, body);
    }

}
