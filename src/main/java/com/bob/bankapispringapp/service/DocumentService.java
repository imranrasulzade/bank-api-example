package com.bob.bankapispringapp.service;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface DocumentService {

    void exportTableToExcel(Integer clientId) throws IOException;

}
