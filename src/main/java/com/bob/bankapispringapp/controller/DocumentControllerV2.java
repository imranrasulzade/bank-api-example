package com.bob.bankapispringapp.controller;

import com.bob.bankapispringapp.model.EmailWithAttachment;
import com.bob.bankapispringapp.service.DocumentServiceV2;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/document-v2")
public class DocumentControllerV2 {
    private final DocumentServiceV2 documentServiceV2;

    @GetMapping("/data-to-excel")
    public void export() throws IOException, ParseException {
        documentServiceV2.exportFromDb();
    }

    @PostMapping(value = "/import-from-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void importToDb(@ModelAttribute MultipartFile file) throws IOException {
        documentServiceV2.importToDbFromExcel(file);
    }


    @PostMapping(value = "/doc-to-email", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void docToEmail(@ModelAttribute EmailWithAttachment email) throws IOException, MessagingException {
        documentServiceV2.docToEmail(email);
    }
}
