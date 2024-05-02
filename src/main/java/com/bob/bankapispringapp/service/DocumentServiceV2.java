package com.bob.bankapispringapp.service;

import com.bob.bankapispringapp.model.EmailWithAttachment;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;

@Service
public interface DocumentServiceV2 {

    void exportFromDb() throws IOException, ParseException;

    void importToDbFromExcel(MultipartFile file) throws IOException;
    void docToEmail(EmailWithAttachment email) throws IOException, MessagingException;


}
