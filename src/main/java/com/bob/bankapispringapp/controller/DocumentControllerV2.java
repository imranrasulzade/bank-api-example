package com.bob.bankapispringapp.controller;

import com.bob.bankapispringapp.service.DocumentService;
import com.bob.bankapispringapp.service.DocumentServiceV2;
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
}
