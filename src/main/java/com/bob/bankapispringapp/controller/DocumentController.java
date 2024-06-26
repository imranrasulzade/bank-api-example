package com.bob.bankapispringapp.controller;

import com.bob.bankapispringapp.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/document")
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping("/data-to-excel")
    public void export(@RequestParam Integer clientId) throws IOException {
        documentService.exportFromDb(clientId);
    }

    @PostMapping(value = "/import-from-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void importToDb(@ModelAttribute MultipartFile file) throws IOException {
        documentService.importToDbFromExcel(file);
    }
}
