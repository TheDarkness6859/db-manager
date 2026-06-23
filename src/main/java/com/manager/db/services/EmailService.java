package com.manager.db.services;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender sender;

    @Value("${spring.mail.username}")
    private String emailSender;

    EmailService(JavaMailSender sender){
        this.sender = sender;
    }

    public void sendEmail (String to, String subject, String body){

        try {

            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(emailSender);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            sender.send(message);
            System.out.println("Email send correctly to: " + to);

        }catch (Exception e){

            System.out.println("Error to send email:" + e.getMessage());

        }

    }

}
