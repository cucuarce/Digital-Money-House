package com.digital_money_house.security_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private final JavaMailSender emailSender;

    private final TemplateEngine templateEngine;
    @Value("${spring.mail.properties.mail.defaultFrom}")
    private String from;

    @Autowired
    public EmailService(JavaMailSender emailSender, TemplateEngine templateEngine) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
    }

    public void sendVerificationEmail(String to, String username, String code) throws MessagingException {
        String htmlContent = processVerificationEmail(username, code);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject("¡Confirma tu correo!");
        helper.setText(htmlContent, true);

        emailSender.send(message);
    }

    private String processVerificationEmail(String username, String code) {
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("code", code);
        return templateEngine.process("verify-email", context);
    }

    public void sendPasswordRecoveryEmail(String to, String username, String recoveryLink) throws MessagingException {
        String htmlContent = processPasswordRecoveryEmail(username, recoveryLink);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject("Recuperación de contraseña");
        helper.setText(htmlContent, true);

        emailSender.send(message);
    }

    private String processPasswordRecoveryEmail(String username, String recoveryLink) {
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("recoveryLink", recoveryLink);
        return templateEngine.process("reset-password", context);
    }

}
