package com.bob.bankapispringapp.service.impl;

import com.bob.bankapispringapp.entity.Client;
import com.bob.bankapispringapp.entity.ClientProperties;
import com.bob.bankapispringapp.exception.EntityNotFoundException;
import com.bob.bankapispringapp.mapper.ClientMapper;
import com.bob.bankapispringapp.model.ClientForExcel;
import com.bob.bankapispringapp.repository.ClientPropertiesRepository;
import com.bob.bankapispringapp.repository.ClientRepository;
import com.bob.bankapispringapp.service.DocumentService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private final ClientRepository clientRepository;
    private final ClientPropertiesRepository clientPropertiesRepository;
    private final ClientMapper clientMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public void exportTableToExcel(Integer clientId) throws IOException {
//        String sql = "WITH temp AS (\n" +
//                "    SELECT \n" +
//                "        client_id,\n" +
//                "        MAX(CASE WHEN property_key = 'location' THEN property_value END) AS location,\n" +
//                "        MAX(CASE WHEN property_key = 'specialty' THEN property_value END) AS specialty\n" +
//                "    FROM client_properties\n" +
//                "    GROUP BY client_id\n" +
//                ")\n" +
//                "\n" +
//                "SELECT c.*, t.location, t.specialty\n" +
//                "FROM client c\n" +
//                "JOIN temp t ON c.id = t.client_id where c.id =" + clientId+";";
//        List<Object[]> data = entityManager.createNativeQuery(sql).getResultList();
//
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("Data");
//
//        int rowCount = 0;
//        for (Object[] rowData : data) {
//            Row row = sheet.createRow(rowCount++);
//            int columnCount = 0;
//            for (Object cellData : rowData) {
//                Cell cell = row.createCell(columnCount++);
//                if (cellData instanceof String) {
//                    cell.setCellValue((String) cellData);
//                } else if (cellData instanceof Integer) {
//                    cell.setCellValue((Integer) cellData);
//                } else if (cellData instanceof Double) {
//                    cell.setCellValue((Double) cellData);
//                } else if (cellData instanceof Date) {
//                    cell.setCellValue((Date) cellData);
//                } else {
//                    cell.setCellValue((String) cellData);
//                }
//
//            }
//        }
//
//        try (FileOutputStream outputStream = new FileOutputStream("data2.xlsx")) {
//            workbook.write(outputStream);
//        }
    }



    @Transactional
    @Override
    public void exportFromDb(Integer clientId) throws IOException {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("client not found with clientID: " + clientId));
        ClientForExcel clientForExcel = clientMapper.toExcelModel(client);
        List<ClientProperties> clientPropertiesList = clientPropertiesRepository
                .findClientPropertiesByClient_Id(clientId);

            Map<String, String> hashMapData = new HashMap<>();
            for(ClientProperties c : clientPropertiesList){
                hashMapData.put(c.getPropertyKey(), c.getPropertyValue());
            }

        Class<?> myClass = clientForExcel.getClass();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet 1");

        Font font = workbook.createFont();
        font.setBold(true);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        Row headerRow = sheet.createRow(0);
        int cellIndex = 0;

        for (Field field : myClass.getDeclaredFields()) {
            Cell cell = headerRow.createCell(cellIndex++);
            cell.setCellValue(field.getName());
            cell.setCellStyle(cellStyle);
        }

        for (String key : hashMapData.keySet()) {
            Cell cell = headerRow.createCell(cellIndex++);
            cell.setCellValue(key);
            cell.setCellStyle(cellStyle);
        }

        // body-ni yaz
        int rowIndex = 1;
            Row row = sheet.createRow(rowIndex++);
            cellIndex = 0;

            for (Field field : myClass.getDeclaredFields()) {
                try {
                    Object value = myClass.newInstance().getClass()
                            .getMethod("get" + toUpperFirstLetter(field.getName())).invoke(clientForExcel);
                    Cell cell = row.createCell(cellIndex++);
                    if (value instanceof String) {
                        cell.setCellValue((String) value);
                    } else if (value instanceof Integer) {
                        cell.setCellValue((Integer) value);
                    } else if (value instanceof Double) {
                        cell.setCellValue((Double) value);
                    } else if (value instanceof Date) {
                        cell.setCellValue((Date) value);
                    } else {
                        cell.setCellValue((String) value);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        for (Map.Entry<String, String> entry : hashMapData.entrySet()) {
            row.createCell(cellIndex++).setCellValue(entry.getValue());
        }

        FileOutputStream fileOutputStream = new FileOutputStream("data.xlsx");
        workbook.write(fileOutputStream);
        fileOutputStream.close();
        log.info("Excel created: data.xlsx");

    }




    public String toUpperFirstLetter(String s) {
        if (s.isEmpty()) {
            return s;
        }
        char first = s.charAt(0);
        if (!Character.isUpperCase(first)) {
            return Character.toUpperCase(first) + s.substring(1);
        } else {
            return s;
        }
    }
}
