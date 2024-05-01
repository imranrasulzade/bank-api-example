package com.bob.bankapispringapp.service.impl;

import com.bob.bankapispringapp.entity.Customer;
import com.bob.bankapispringapp.entity.CustomerProps;
import com.bob.bankapispringapp.repository.CustomerPropsRepository;
import com.bob.bankapispringapp.repository.CustomerRepository;
import com.bob.bankapispringapp.service.DocumentServiceV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImplV2 implements DocumentServiceV2 {
    private final CustomerRepository customerRepository;
    private final CustomerPropsRepository customerPropsRepository;

    @Override
    public void exportFromDb() throws IOException, ParseException {

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Customers");

        List<Customer> customers = customerRepository.findAll();
        Map<String, Integer> propertyKeyToColumnIndex = new HashMap<>();
        Row headerRow = sheet.createRow(0);

        fillHeader(customers, headerRow, propertyKeyToColumnIndex);

        int rowIndex = 1;
        for (Customer customer : customers) {
            Row dataRow = sheet.createRow(rowIndex++);
            fillCustomerToRow(customer, dataRow, propertyKeyToColumnIndex);
        }
        createAndWriteExcelFile(workbook, "customers");
    }


    @Override
    public void importToDbFromExcel(MultipartFile file) throws IOException {
        String excelFilePath = convertMultipartFileToPath(file);
        List<Customer> customers = readCustomerDataFromExcel(excelFilePath);

        for (Customer customer : customers) {
            List<CustomerProps> customerPropsList = customer.getCustomerPropsList();
            customerPropsList.forEach(props ->{
                Customer tempCustomer = new Customer();
                tempCustomer.setId(customer.getId());
                props.setCustomer(tempCustomer);
            });
            List<CustomerProps> customerPropsDB = customerPropsRepository.findByCustomerId(customer.getId());
            List<CustomerProps> filteredList = removeDuplicateCustomerProps(customerPropsList, customerPropsDB);
            filteredList.forEach(customerProps -> customerProps.setCustomer(customer));
            customer.setCustomerPropsList(filteredList);
        }
        customerRepository.saveAll(customers);
        log.info("customer list saved all");

    }

    private void fillHeader(List<Customer> customers, Row headerRow, Map<String, Integer> propertyKeyToColumnIndex) {
        Cell cell = headerRow.createCell(0);
        cell.setCellValue("ID");
        cell = headerRow.createCell(1);
        cell.setCellValue("Name");
        cell = headerRow.createCell(2);
        cell.setCellValue("Surname");
        cell = headerRow.createCell(3);
        cell.setCellValue("Timestamp");

        int cellIndex = 4;

        for (Customer customer : customers) {
            List<CustomerProps> props = customer.getCustomerPropsList();
            for (CustomerProps prop : props) {
                String propKey = prop.getPropertyKey();
                if (!propertyKeyToColumnIndex.containsKey(propKey)) {
                    cell = headerRow.createCell(cellIndex);
                    cell.setCellValue(propKey);
                    propertyKeyToColumnIndex.put(propKey, cellIndex++);
                }
            }
        }
    }

    private void fillCustomerToRow(Customer customer, Row dataRow, Map<String, Integer> propertyKeyToColumnIndex) {
        Cell cell1 = dataRow.createCell(0);
        cell1.setCellValue(customer.getId());
        cell1 = dataRow.createCell(1);
        cell1.setCellValue(customer.getName());
        cell1 = dataRow.createCell(2);
        cell1.setCellValue(customer.getSurname());
        cell1 = dataRow.createCell(3);
        Date timestamp = parseLocalDateToDate((customer.getTimestamp()));
        CreationHelper creationHelper = cell1.getSheet().getWorkbook().getCreationHelper();
        CellStyle dateCellStyle = cell1.getSheet().getWorkbook().createCellStyle();
        dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd"));
        cell1.setCellStyle(dateCellStyle);
        cell1.setCellValue(timestamp);

        for (CustomerProps prop : customer.getCustomerPropsList()) {
            String propKey = prop.getPropertyKey();
            String propValue = prop.getPropertyValue();
            Integer columnIndex = propertyKeyToColumnIndex.get(propKey);
            if (columnIndex != null) {
                cell1 = dataRow.createCell(columnIndex);
                cell1.setCellValue(propValue);
            }
        }
    }

    private void createAndWriteExcelFile(Workbook workbook, String fileName) {
        try (FileOutputStream outputStream = new FileOutputStream(fileName + ".xlsx")) {
            workbook.write(outputStream);
            log.info("Data written to " + fileName + ".xlsx successfully!");
        } catch (IOException e) {
            throw new RuntimeException("data can not written to excel file! reason: " + e.getMessage());
        }
    }

    private String convertMultipartFileToPath(MultipartFile multipartFile) throws IOException {
        Path tempFilePath = Files.createTempFile("temp_data", ".xlsx");
        multipartFile.transferTo(tempFilePath);
        return tempFilePath.toAbsolutePath().toString();
    }


    private Date parseLocalDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private LocalDate parseDateToLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }


    private List<Customer> readCustomerDataFromExcel(String excelFilePath) throws IOException {
        List<Customer> customers = new ArrayList<>();
        try (FileInputStream inputStream = new FileInputStream(excelFilePath)) {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(0);

            Map<Integer, String> propertyKeyIndexMap = new HashMap<>();
            for (int cellIndex = 4; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
                Cell headerCell = headerRow.getCell(cellIndex);
                if (headerCell != null) {
                    String propertyKey = headerCell.getStringCellValue();
                    propertyKeyIndexMap.put(cellIndex, propertyKey);
                }
            }

            int numRows = sheet.getLastRowNum();
            for (int rowIndex = 1; rowIndex <= numRows; rowIndex++) {
                Row dataRow = sheet.getRow(rowIndex);

                Customer customer = new Customer();
                if (dataRow.getCell(0) != null) {
                    customer.setId((int) dataRow.getCell(0).getNumericCellValue());
                }
                customer.setName(dataRow.getCell(1).getStringCellValue());
                customer.setSurname(dataRow.getCell(2).getStringCellValue());
                customer.setTimestamp(parseDateToLocalDate(dataRow.getCell(3).getDateCellValue()));

                // props-u yigiram
                Map<String, String> propertyMap = new HashMap<>();
                for (Map.Entry<Integer, String> entry : propertyKeyIndexMap.entrySet()) {
                    int cellIndex = entry.getKey();
                    String propertyKey = entry.getValue();
                    Cell propertyCell = dataRow.getCell(cellIndex);
                    String propertyValue = propertyCell != null ? propertyCell.getStringCellValue() : null;
                    if (propertyValue != null) {
                        propertyMap.put(propertyKey, propertyValue);
                    }
                }

                // propsu customere add edirem
                for (Map.Entry<String, String> propertyEntry : propertyMap.entrySet()) {
                    String propertyKey = propertyEntry.getKey();
                    String propertyValue = propertyEntry.getValue();
                    CustomerProps customerProps = new CustomerProps();
//                    customerProps.setCustomer(customer); //acsam stackOverFlow alacam
                    customerProps.setPropertyKey(propertyKey);
                    customerProps.setPropertyValue(propertyValue);
                    if (customer.getCustomerPropsList() == null) {
                        customer.setCustomerPropsList(new ArrayList<>());
                    }
                    customer.getCustomerPropsList().add(customerProps);
                }

                customers.add(customer);
            }
        }

        return customers;
    }


    private List<CustomerProps> removeDuplicateCustomerProps(List<CustomerProps> list1, List<CustomerProps> list2) {
        List<CustomerProps> filteredList = new ArrayList<>(list1);

        for (CustomerProps customerProps1 : list1) {
            for (CustomerProps customerProps2 : list2) {
//                if (customerProps1.getCustomer() != null) {//ehtiyac yoxdu
                    if (customerProps1.getCustomer().getId().equals(customerProps2.getCustomer().getId())
                            && customerProps1.getPropertyKey().equals(customerProps2.getPropertyKey())
                            && customerProps1.getPropertyValue().equals(customerProps2.getPropertyValue())) {
                        filteredList.remove(customerProps1);
                        break;
                    }
//                }
            }
        }
        return filteredList;
    }

//    private List<CustomerProps> updateIdCustomerProps(List<CustomerProps> uniquePropsList, List<CustomerProps> customerPropsDB) {
//        for (CustomerProps uniqueClientProps : uniquePropsList) {
//            for (CustomerProps customerProps : customerPropsDB) {
//                if (uniqueClientProps.getPropertyKey().equals(customerProps.getPropertyKey())) {
//                    uniqueClientProps.setId(customerProps.getId());
//                    break;
//                }
//            }
//        }
//        return uniquePropsList;
//    }


}
