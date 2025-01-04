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
    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    @Autowired
    public KafkaService(ObjectMapper objectMapper, EmailService emailService) {
        this.objectMapper = objectMapper;
        this.emailService = emailService;
    }

    @KafkaListener(topics = "email-topic", groupId = "emailService")
    public void emailQueue(String emailDto) throws JsonProcessingException {
        SendEmailDto emailDtos = objectMapper.readValue(emailDto, SendEmailDto.class);
        String from = emailDtos.getFrom();
        String to = emailDtos.getTo();
        String subject = emailDtos.getSubject();
        String body = emailDtos.getBody();

        emailService.sendEmail(from, to, subject, body);
    }

}
