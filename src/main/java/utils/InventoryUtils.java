package utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import io.restassured.path.json.JsonPath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InventoryUtils {

    private static final String FILE_PATH = System.getProperty("user.dir") + "/src/test/resources/InventoryIds.xlsx";
    private static final String SHEET_NAME = "Inventory";

    // Clear old inventory IDs before writing new ones
    public static void clearInventoryIds() {
        try {
            File file = new File(FILE_PATH);
            XSSFWorkbook workbook;
            XSSFSheet sheet;

            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    workbook = new XSSFWorkbook(fis);
                }
            } else {
                workbook = new XSSFWorkbook();
            }

            sheet = workbook.getSheet(SHEET_NAME);
            if (sheet != null) {
                workbook.removeSheetAt(workbook.getSheetIndex(sheet));
            }

            sheet = workbook.createSheet(SHEET_NAME);

            // Create header row
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("SNo");
            header.createCell(1).setCellValue("ObjectId");

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
            workbook.close();
            System.out.println("✅ Inventory Excel cleared and header created");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Write IDs starting from row 2 (index 1)
    public static void writeInventoryIds(List<String> ids) {
        try {
            File file = new File(FILE_PATH);
            XSSFWorkbook workbook;
            XSSFSheet sheet;

            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    workbook = new XSSFWorkbook(fis);
                }
            } else {
                workbook = new XSSFWorkbook();
            }

            sheet = workbook.getSheet(SHEET_NAME);
            if (sheet == null) {
                sheet = workbook.createSheet(SHEET_NAME);
                // Create header if missing
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("SNo");
                header.createCell(1).setCellValue("ObjectId");
            }

            int rowIndex = 1; // start from row 2
            for (int i = 0; i < ids.size(); i++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) row = sheet.createRow(rowIndex);

                row.createCell(0).setCellValue(i + 1); // SNo
                row.createCell(1).setCellValue(ids.get(i)); // ObjectId

                rowIndex++;
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
            workbook.close();
            System.out.println("✅ " + ids.size() + " Inventory IDs written to Excel");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
   // ====== Extract terminal data from JSON and write directly to TerminalDetails sheet ======
    @SuppressWarnings("unchecked")
    public static void extractTerminalData(JsonPath json, String objectId) {
        try {
            File file = new File(FILE_PATH);
            XSSFWorkbook workbook;
            XSSFSheet sheet;
            String TERMINAL_SHEET = "TerminalDetails";

            // Load or create workbook
            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    workbook = new XSSFWorkbook(fis);
                }
            } else {
                workbook = new XSSFWorkbook();
            }

            // Load or create sheet
            sheet = workbook.getSheet(TERMINAL_SHEET);
            if (sheet == null) {
                sheet = workbook.createSheet(TERMINAL_SHEET);
                // Header row
                Row headerRow = sheet.createRow(0);
                String[] headers = {"SNo","ObjectId","Merchant ID","Merchant Status","Terminal ID","Terminal Status","Device SNo","Device Model"};
                for (int i=0; i<headers.length; i++) headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowIndex = sheet.getLastRowNum() + 1;
            int sno = rowIndex;

            Map<String, Object> merchant = json != null ? json.getMap("merchants") : null;

            if (merchant == null) {
                // Failed or empty response
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(sno++);
                row.createCell(1).setCellValue(objectId);
                row.createCell(2).setCellValue("");
                row.createCell(3).setCellValue("No response");
                row.createCell(4).setCellValue("");
                row.createCell(5).setCellValue("");
                row.createCell(6).setCellValue("");
                row.createCell(7).setCellValue("");
            } else {
                String merchantId = merchant.getOrDefault("merchantId","").toString();
                String merchantStatus = merchant.getOrDefault("status","").toString();
                List<Map<String,Object>> terminals = (List<Map<String,Object>>) merchant.get("terminalInfo");

                if (terminals == null || terminals.isEmpty()) {
                    Row row = sheet.createRow(rowIndex++);
                    row.createCell(0).setCellValue(sno++);
                    row.createCell(1).setCellValue(objectId);
                    row.createCell(2).setCellValue(merchantId);
                    row.createCell(3).setCellValue(merchantStatus);
                    row.createCell(4).setCellValue("");
                    row.createCell(5).setCellValue("");
                    row.createCell(6).setCellValue("");
                    row.createCell(7).setCellValue("");
                } else {
                    for (Map<String,Object> terminal : terminals) {
                        Row row = sheet.createRow(rowIndex++);
                        row.createCell(0).setCellValue(sno++);
                        row.createCell(1).setCellValue(objectId);
                        row.createCell(2).setCellValue(merchantId);
                        row.createCell(3).setCellValue(merchantStatus);
                        row.createCell(4).setCellValue(terminal.getOrDefault("terminalId","").toString());
                        row.createCell(5).setCellValue(terminal.getOrDefault("status","").toString());

                        Map<String,Object> editedDetails = (Map<String,Object>) terminal.get("editedDetails");
                        row.createCell(6).setCellValue(editedDetails != null ? editedDetails.getOrDefault("deviceNumber","").toString() : "");
                        row.createCell(7).setCellValue(editedDetails != null ? editedDetails.getOrDefault("deviceModel","").toString() : "");
                    }
                }
            }

            // Write workbook
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
            workbook.close();
            System.out.println("✅ Terminal data appended for ObjectId: " + objectId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    
 }
    
    
    
    
    
    
}