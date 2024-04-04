package com.bob.bankapispringapp.controller;

import com.bob.bankapispringapp.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/document")
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping("/import-excel")
    public void exportTableToExcel(@RequestParam String tableName) throws IOException {
        documentService.exportTableToExcel(tableName);
    }
}
