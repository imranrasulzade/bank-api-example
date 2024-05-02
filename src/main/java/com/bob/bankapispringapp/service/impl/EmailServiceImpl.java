package com.bob.bankapispringapp.service.impl;

import com.bob.bankapispringapp.model.EmailWithAttachment;
import com.bob.bankapispringapp.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;

    public void sendEmailWithAttachment(EmailWithAttachment emailWithAttachment) throws MessagingException, IOException {
        if(emailWithAttachment.getFile().getOriginalFilename() == null){
            throw new RuntimeException("file-name can not be null");
        }
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(emailWithAttachment.getReceiver());
        helper.setSubject(emailWithAttachment.getSubject());
        helper.setText(emailWithAttachment.getText());

        helper.addAttachment(emailWithAttachment.getFile().getOriginalFilename(),
                new ByteArrayResource(emailWithAttachment.getFile().getBytes()));

        emailSender.send(message);
        log.info("email sent to {} successfully!", emailWithAttachment.getReceiver());
    }
}
