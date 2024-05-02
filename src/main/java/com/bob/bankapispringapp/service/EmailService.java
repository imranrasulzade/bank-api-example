package com.bob.bankapispringapp.service;

import com.bob.bankapispringapp.model.EmailWithAttachment;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface EmailService {

    void sendEmailWithAttachment(EmailWithAttachment emailWithAttachment) throws MessagingException, IOException;

}
