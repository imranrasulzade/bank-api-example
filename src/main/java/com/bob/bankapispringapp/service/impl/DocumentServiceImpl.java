package com.bob.bankapispringapp.service.impl;

import com.bob.bankapispringapp.service.DocumentService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void exportTableToExcel(Integer clientId) throws IOException {
        String sql = "WITH temp AS (\n" +
                "    SELECT \n" +
                "        client_id,\n" +
                "        MAX(CASE WHEN property_key = 'location' THEN property_value END) AS location,\n" +
                "        MAX(CASE WHEN property_key = 'specialty' THEN property_value END) AS specialty\n" +
                "    FROM client_properties\n" +
                "    GROUP BY client_id\n" +
                ")\n" +
                "\n" +
                "SELECT c.*, t.location, t.specialty\n" +
                "FROM client c\n" +
                "JOIN temp t ON c.id = t.client_id where c.id =" + clientId+";";
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
                } else if (cellData instanceof Date) {
                    cell.setCellValue((Date) cellData);
                } else {
                    cell.setCellValue((String) cellData);
                }


                // Handle other data types as needed

            }
        }

        try (FileOutputStream outputStream = new FileOutputStream("data2.xlsx")) {
            workbook.write(outputStream);
        }
    }
}
