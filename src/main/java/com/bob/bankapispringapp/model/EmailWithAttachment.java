package com.bob.bankapispringapp.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class EmailWithAttachment {
    private String receiver;
    private String subject;
    private String text;
    private MultipartFile file;
}