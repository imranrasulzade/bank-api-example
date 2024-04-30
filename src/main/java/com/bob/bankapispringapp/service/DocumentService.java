package com.bob.bankapispringapp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;

@Service
public interface DocumentService {

    void exportFromDb(Integer clientId) throws IOException;

    void importToDbFromExcel(MultipartFile file) throws IOException;

}
