package com.bob.bankapispringapp.service.impl;

import com.bob.bankapispringapp.repository.CustomerPropsRepository;
import com.bob.bankapispringapp.repository.CustomerRepository;
import com.bob.bankapispringapp.service.DocumentServiceV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImplV2 implements DocumentServiceV2 {
    private final CustomerRepository customerRepository;
    private final CustomerPropsRepository customerPropsRepository;

    @Override
    public void exportFromDb() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet 1");

        Font font = workbook.createFont();
        font.setBold(true);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        Row headerRow = sheet.createRow(0);
        //alqoritm fayla yaz
        //set hissede reflect ile value null olarsa set "".



        //ByteArray ile yaz
        FileOutputStream fileOutputStream = new FileOutputStream("customers.xlsx");
        workbook.write(fileOutputStream);
        fileOutputStream.close();
        log.info("Excel created: customers.xlsx");
        //fayli mail ile gonder
    }






    @Override
    public void importToDbFromExcel(MultipartFile file) throws IOException {
        String excelFilePath = convertMultipartFileToPath(file);
        //Workbook achilir
        try(XSSFWorkbook workbook = new XSSFWorkbook(excelFilePath)){
            XSSFSheet sheet = workbook.getSheetAt(0);
            //alqoritm fayldan oxumaq


        }catch (Exception e){
            log.error("Error due to -> {}", e.getMessage());
        }




    }

    private String convertMultipartFileToPath(MultipartFile multipartFile) throws IOException {
        Path tempFilePath = Files.createTempFile("temp_data", ".xlsx");
        multipartFile.transferTo(tempFilePath);
        return tempFilePath.toAbsolutePath().toString();
    }
}
