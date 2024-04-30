package com.bob.bankapispringapp.service.impl;

import com.bob.bankapispringapp.entity.Client;
import com.bob.bankapispringapp.entity.ClientProperties;
import com.bob.bankapispringapp.exception.EntityNotFoundException;
import com.bob.bankapispringapp.mapper.ClientMapper;
import com.bob.bankapispringapp.model.ClientForExcel;
import com.bob.bankapispringapp.repository.ClientPropertiesRepository;
import com.bob.bankapispringapp.repository.ClientRepository;
import com.bob.bankapispringapp.service.DocumentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private final ClientRepository clientRepository;
    private final ClientPropertiesRepository clientPropertiesRepository;
    private final ClientMapper clientMapper;


    @Transactional
    @Override
    public void exportFromDb(Integer clientId) throws IOException {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("client not found with clientID: " + clientId));
        ClientForExcel clientForExcel = clientMapper.toExcelModel(client);
        List<ClientProperties> clientPropertiesList = clientPropertiesRepository
                .findClientPropertiesByClient_Id(clientId);

        Map<String, String> hashMapData = new HashMap<>();
        for (ClientProperties c : clientPropertiesList) {
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
            cell.setCellValue(field.getName().toUpperCase());
            cell.setCellStyle(cellStyle);
        }

        for (String key : hashMapData.keySet()) {
            Cell cell = headerRow.createCell(cellIndex++);
            cell.setCellValue(key.toUpperCase());
            cell.setCellStyle(cellStyle);
        }

        // body-ni yaz
        int rowIndex = 1;
        Row row = sheet.createRow(rowIndex);
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
                    CreationHelper creationHelper = cell.getSheet().getWorkbook().getCreationHelper();
                    CellStyle dateCellStyle = cell.getSheet().getWorkbook().createCellStyle();
                    dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd"));
                    cell.setCellStyle(dateCellStyle);
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

    @Override
    public void importToDbFromExcel(MultipartFile file) throws IOException {
        log.info("importToDbFromExcel method started");
        String excelFilePath = convertMultipartFileToPath(file);
        ClientForExcel clientForExcel = readExcelDataWithReflectForClass(excelFilePath);

        Client clientDB = clientRepository.findByUsername(clientForExcel.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Client not fount with this username: "
                        + clientForExcel.getUsername()));

        Client client = clientMapper.toEntityFromExcel(clientForExcel);
        client.setId(clientDB.getId());
        client.setPassword(clientDB.getPassword());
        client.setAuthorities(clientDB.getAuthorities());

        //***props
        HashMap<String, String> hashMap = fillHashMapFromExcel(ClientForExcel.class.getDeclaredFields().length, excelFilePath);
        List<ClientProperties> clientPropertiesList = fillListFromHashMap(hashMap);

        List<ClientProperties> clientPropsDB = clientPropertiesRepository
                .findClientPropertiesByClient_Id(client.getId());

        List<ClientProperties> uniquePropsList = removeDuplicateClientProperties(clientPropertiesList, clientPropsDB);

        List<ClientProperties> updatedList = updateIdClientProperties(uniquePropsList, clientPropsDB);

        updatedList.forEach(props -> props.setClient(client));
        client.setClientPropertiesList(updatedList);
//        System.out.println(updatedList);
        clientRepository.save(client);
        log.info("Client updated from excel data. Updated client username: {}", client.getUsername());
    }


    private List<ClientProperties> removeDuplicateClientProperties(List<ClientProperties> list1, List<ClientProperties> list2) {
        List<ClientProperties> filteredList = new ArrayList<>(list1);

        for (ClientProperties clientProperties1 : list1) {
            for (ClientProperties clientProperties2 : list2) {
                if (clientProperties1.getPropertyKey().equals(clientProperties2.getPropertyKey()) &&
                        clientProperties1.getPropertyValue().equals(clientProperties2.getPropertyValue())) {
                    filteredList.remove(clientProperties1);
                    break;
                }
            }
        }
        return filteredList;
    }

    private List<ClientProperties> updateIdClientProperties(List<ClientProperties> uniquePropsList, List<ClientProperties> clientPropsDB) {
        for (ClientProperties uniqueClientProps : uniquePropsList) {
            for (ClientProperties dbClientProps : clientPropsDB) {
                if (uniqueClientProps.getPropertyKey().equals(dbClientProps.getPropertyKey())) {
                    uniqueClientProps.setId(dbClientProps.getId());
                    break;
                }
            }
        }
        return uniquePropsList;
    }

    private String convertMultipartFileToPath(MultipartFile multipartFile) throws IOException {
        Path tempFilePath = Files.createTempFile("temp_data", ".xlsx");
        multipartFile.transferTo(tempFilePath);
        return tempFilePath.toAbsolutePath().toString();
    }

    private ClientForExcel readExcelDataWithReflectForClass(String filePath) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(filePath);
        XSSFSheet sheet = workbook.getSheetAt(0);

//        int headerRow = 0;
        int dataStartRow = 1;

        Class<?> clientClass = ClientForExcel.class;
        Field[] fields = clientClass.getDeclaredFields();
        ClientForExcel client = new ClientForExcel();
        Row row = sheet.getRow(dataStartRow);

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            String fieldType = field.getType().getSimpleName();
            String cellValue = switch (fieldType) {
                case "Integer" -> String.valueOf((int) row.getCell(i).getNumericCellValue());
                case "String" -> row.getCell(i).getStringCellValue();
                case "Date" -> String.valueOf(row.getCell(i).getDateCellValue());
                default -> null;
            };

            try {
                setValueToClassField(field, client, cellValue);
            } catch (IllegalAccessException e) {
                log.error("error due to -> {}", e.getMessage());
            } catch (ParseException e) {
                log.error("Error due to -> {}", e.getMessage());
            }
        }
        workbook.close();
        return client;
    }

    private void setValueToClassField(Field field, Object object, String cellValue) throws ParseException, IllegalAccessException {
        if (field.getType().getSimpleName().equals("String")) {
            field.set(object, cellValue);
        } else if (field.getType().getSimpleName().equals("Date")) {
            Date date = parseDate(cellValue);
            field.set(object, date);
        } else {
            throw new RuntimeException("Unexpected data type for field");
        }
    }

    private Date parseDate(String dateString) throws ParseException {
        String format = "EEE MMM dd HH:mm:ss z yyyy";
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.UK);
        return formatter.parse(dateString);
    }

    private HashMap<String, String> fillHashMapFromExcel(Integer classFieldCount, String filePath) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(filePath);
        XSSFSheet sheet = workbook.getSheetAt(0);

        Row headerRow = sheet.getRow(0);

        HashMap<String, String> hashMap = new HashMap<>();

        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);

            for (int cellIndex = classFieldCount; cellIndex < row.getLastCellNum(); cellIndex++) {
                Cell headerCell = headerRow.getCell(cellIndex);
                Cell dataCell = row.getCell(cellIndex);

                if (dataCell == null) {
                    continue;
                }

                String header = headerCell.getStringCellValue();
                String data = getCellValueAsString(dataCell);

                hashMap.put(header.toLowerCase(), data);
            }
        }
        workbook.close();
        return hashMap;
    }

    private List<ClientProperties> fillListFromHashMap(HashMap<String, String> hashMap) {
        List<ClientProperties> clientPropertiesList = new ArrayList<>();

        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            String propertyKey = entry.getKey();
            String propertyValue = entry.getValue();

            ClientProperties clientProperties = new ClientProperties();
            clientProperties.setPropertyKey(propertyKey);
            clientProperties.setPropertyValue(propertyValue);
            clientPropertiesList.add(clientProperties);
        }
//
        return clientPropertiesList;
    }

    private String getCellValueAsString(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private String toUpperFirstLetter(String s) {
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
