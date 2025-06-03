package com.takarub.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.component.datatable.DataTable;

/**
 *
 * @author aalnadi
 */
public class ExcelConfig {

    public void prepareDownload(UIData dataTable) throws IOException, InvalidFormatException {
        FacesContext context = FacesContext.getCurrentInstance();
        dataTable = (UIData) context.getViewRoot().findComponent("form:dataTableTest");

        if (dataTable == null) {
            System.out.println("DataTable not found in view!");
            return;
        }

        downloadDataTableData("myData", dataTable); // Call download method
    }

    public void downloadDataTableData(String filename, UIData dataTable) throws IOException, InvalidFormatException {
        List<?> data = (List<?>) dataTable.getValue();
        List<String> columnNames = getColumnNames(dataTable);

        if (data == null || data.isEmpty() || columnNames == null || columnNames.isEmpty()) {
            return;
        }

        InputStream templateStream = getClass().getClassLoader().getResourceAsStream("templates/templet.xlsm");
        if (templateStream == null) {
            throw new IOException("Template file not found");
        }

        OPCPackage pkg = OPCPackage.open(templateStream);
        XSSFWorkbook workbook = new XSSFWorkbook(pkg);
        XSSFSheet sheet = workbook.getSheet("sheet1");

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columnNames.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnNames.get(i));
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("d/MM/yyyy"); // Format for date

        for (int i = 0; i < data.size(); i++) {
            Row dataRow = sheet.createRow(i + 1);
            Object item = data.get(i);
            for (int j = 0; j < columnNames.size(); j++) {
                Cell cell = dataRow.createCell(j);
                try {
                    String fieldName = columnNames.get(j);
                    String formattedFieldName = convertToCamelCase(fieldName);
                    String getterMethod = "get" + formattedFieldName.substring(0, 1).toUpperCase() + formattedFieldName.substring(1);

                    Object value = null;
                    try {
                        java.lang.reflect.Method method = item.getClass().getMethod(getterMethod);
                        value = method.invoke(item);
                    } catch (NoSuchMethodException e) {
                        try {
                            java.lang.reflect.Field field = item.getClass().getDeclaredField(formattedFieldName);
                            field.setAccessible(true);
                            value = field.get(item);
                        } catch (NoSuchFieldException ex) {
                            System.out.println("Field not found: " + formattedFieldName);
                        }
                    }

                    if (value != null) {
                        if (value instanceof Date) {
                            cell.setCellValue(dateFormat.format((Date) value)); // Format date before writing
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    } else {
                        cell.setCellValue("Error");
                    }

                } catch (IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
                    cell.setCellValue("Error");
                }
            }
        }

        String userHome = System.getProperty("user.home");
        Path downloadPath = Paths.get(userHome, "Downloads", filename + ".xlsm");

        if (!Files.exists(downloadPath.getParent())) {
            Files.createDirectories(downloadPath.getParent());
        }

        try (OutputStream fileOut = Files.newOutputStream(downloadPath, StandardOpenOption.CREATE)) {
            workbook.write(fileOut);
            FacesContext.getCurrentInstance().addMessage("form:growl",
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Download Successful", "The file has been downloaded."));
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage("form:growl",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Download not Successful", "The file has not been downloaded."));
        } finally {
            workbook.close();
            pkg.close();
        }
    }

    private List<String> getColumnNames(UIData dataTable) {
        List<String> columnNames = new ArrayList<>();

        if (dataTable instanceof DataTable) { // Check if it's a PrimeFaces DataTable
            DataTable pfDataTable = (DataTable) dataTable; // Cast to PrimeFaces DataTable

            for (org.primefaces.component.api.UIColumn column : pfDataTable.getColumns()) { // Use PrimeFaces Column
                String headerText = column.getHeaderText();
                if (headerText != null) {
                    columnNames.add(headerText);
                }
            }
        } else {
            // Handle the case where it's not a PrimeFaces DataTable (optional)
            System.out.println("Not a PrimeFaces DataTable");
        }

        return columnNames;
    }

    private String convertToCamelCase(String columnName) {
        // Remove spaces, special characters, and ensure camelCase
        columnName = columnName.replaceAll("[^a-zA-Z0-9]", " "); // Replace non-alphanumeric with space
        String[] words = columnName.toLowerCase().split("\\s+"); // Split by space
        StringBuilder camelCase = new StringBuilder(words[0]); // First word remains lowercase

        // Capitalize the first letter of each following word
        for (int i = 1; i < words.length; i++) {
            camelCase.append(words[i].substring(0, 1).toUpperCase()).append(words[i].substring(1));
        }

        return camelCase.toString();
    }
}
