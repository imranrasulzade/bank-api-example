package com.bob.bankapispringapp.service.impl;

import com.bob.bankapispringapp.service.DocumentService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void exportTableToExcel(String tableName) throws IOException {
        String sql = "SELECT * FROM " + tableName;
        List<Object[]> data = entityManager.createNativeQuery(sql).getResultList();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        int rowCount = 0;
        for (Object[] rowData : data) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            for (Object cellData : rowData) {
                Cell cell = row.createCell(columnCount++);
                if (cellData instanceof String) {
                    cell.setCellValue((String) cellData);
                } else if (cellData instanceof Integer) {
                    cell.setCellValue((Integer) cellData);
                } else if (cellData instanceof Double) {
                    cell.setCellValue((Double) cellData);
                } // Handle other data types as needed
            }
        }

        try (FileOutputStream outputStream = new FileOutputStream("data1.xlsx")) {
            workbook.write(outputStream);
        }
    }
}
