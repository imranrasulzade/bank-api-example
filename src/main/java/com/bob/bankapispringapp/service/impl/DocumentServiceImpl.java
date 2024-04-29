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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
//        System.out.println(clientForExcel);
        Client clientDB = clientRepository.findByUsername(clientForExcel.getUsername())
                .orElseThrow(()-> new EntityNotFoundException("Client not fount with this username: "
                        + clientForExcel.getUsername()));
        Client client = clientMapper.toEntityFromExcel(clientForExcel);
//        System.out.println(client);
        client.setId(clientDB.getId());
        client.setPassword(clientDB.getPassword());
        client.setAuthorities(clientDB.getAuthorities());
//        System.out.println(client);



        //***
        HashMap<String, String> hashMap = fillHashMapFromExcel(ClientForExcel.class.getDeclaredFields().length, excelFilePath);
//        System.out.println(hashMap);
        List<ClientProperties> clientPropertiesList = fillListFromHashMap(hashMap);
        List<ClientProperties> clientPropertiesFromDbList = clientPropertiesRepository
                .findClientPropertiesByClient_Id(client.getId());
        List<ClientProperties> uniqueList = removeDuplicateClientProperties(clientPropertiesList, clientPropertiesFromDbList);
        clientPropertiesList.forEach(props -> props.setClient(client));

        List<ClientProperties> updatedList = updateClientProperties(clientPropertiesList, clientPropertiesFromDbList);
        client.setClientPropertiesList(updatedList);
//        System.out.println(updatedList); //stackoverflow
//        clientRepository.save(client);
//        clientPropertiesRepository.saveAll(clientPropertiesList);
        log.info("Client updated from excel data. Updated client username: {}", clientDB.getUsername());
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

    private List<ClientProperties> updateClientProperties(List<ClientProperties> list1, List<ClientProperties> list2) {
        for (ClientProperties clientProperties1 : list1) {
            for (ClientProperties clientProperties2 : list2) {
                if (clientProperties1.getPropertyKey().equals(clientProperties2.getPropertyKey()) &&
                        !clientProperties1.getPropertyValue().equals(clientProperties2.getPropertyValue())) {
                    clientProperties1.setPropertyValue(clientProperties1.getPropertyValue());//stackoverflow
                    break;
                }
            }
        }

        return list1;
    }

    private String convertMultipartFileToPath(MultipartFile multipartFile) throws IOException {
        Path tempFilePath = Files.createTempFile("temp_data", ".xlsx");
        multipartFile.transferTo(tempFilePath);
        return tempFilePath.toAbsolutePath().toString();
    }


    private ClientForExcel readExcelDataWithReflectForClass(String filePath) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(filePath);
        XSSFSheet sheet = workbook.getSheetAt(0);

        int headerRow = 0;
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
//                field.set(client, cellValue);
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
        if(field.getType().getSimpleName().equals("String")){
            field.set(object, cellValue);
        }else if(field.getType().getSimpleName().equals("Date")){
            Date date = parseDate(cellValue);
            field.set(object, date);
        }else {
            throw new RuntimeException("Unexpected data type for field");
        }
    }

    private Date parseDate(String dateString) throws ParseException {
        String format = "EEE MMM dd HH:mm:ss z yyyy";
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.UK);
        Date date = formatter.parse(dateString);
        return date;
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

//        System.out.println(hashMap);
        workbook.close();
        return hashMap;
    }

    private List<ClientProperties> fillListFromHashMap(HashMap<String, String> hashMap){
        List<ClientProperties> clientPropertiesList = new ArrayList<>();

        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            String propertyKey = entry.getKey();
            String propertyValue = entry.getValue();

            ClientProperties clientProperties = new ClientProperties();
            //client id ve normal id elave etmeliyem. varsa bazadakini yenilemeli yoxdusa yeni insert atmaliyam
            clientProperties.setPropertyKey(propertyKey);
            clientProperties.setPropertyValue(propertyValue);

            clientPropertiesList.add(clientProperties);
        }
//        System.out.println(clientPropertiesList);
        return clientPropertiesList;
    }

    private String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
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


//    private ClientForExcel readExcelData(String filePath) throws IOException {
//        XSSFWorkbook workbook = new XSSFWorkbook(filePath);
//        XSSFSheet sheet = workbook.getSheetAt(0);
//
//        // ilk setir
//        int headerRow = 0;
//        int dataStartRow = 1;
//
//        // setir oxu
//        ClientForExcel clientForExcel = new ClientForExcel();
//        Row row = sheet.getRow(dataStartRow);
////        clientForExcel.setId((int) row.getCell(0).getNumericCellValue());
//        clientForExcel.setUsername(row.getCell(1).getStringCellValue());
//        clientForExcel.setName(row.getCell(2).getStringCellValue());
//        clientForExcel.setSurname(row.getCell(3).getStringCellValue());
//        clientForExcel.setEmail(row.getCell(4).getStringCellValue());
//        clientForExcel.setPhone(row.getCell(5).getStringCellValue());
////        clientForExcel.setBirthdate(row.getCell(6).getDateCellValue());
////        clientForExcel.setStatus((int) row.getCell(7).getNumericCellValue());
//
//
//        return clientForExcel;
//    }





}
