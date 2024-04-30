package com.bob.bankapispringapp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface DocumentServiceV2 {

    void exportFromDb() throws IOException;

    void importToDbFromExcel(MultipartFile file) throws IOException;
}
