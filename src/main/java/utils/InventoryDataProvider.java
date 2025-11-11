package utils;
import org.testng.annotations.DataProvider;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class InventoryDataProvider {
	
	
	private static final String FILE_PATH = System.getProperty("user.dir") + "/src/test/resources/InventoryIds.xlsx";
    private static final String SHEET_NAME = "Inventory";
    public static int reference=0;

    @DataProvider(name = "InventoryIds")
    public static Iterator<Object[]> provideInventoryIds() {
        List<Object[]> data = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(FILE_PATH));
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(SHEET_NAME);
            if (sheet == null) {
                System.err.println("❌ Sheet not found: " + SHEET_NAME);
                return data.iterator();
            }

            // Start from row 2 (index 1) because row 0 is header
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell cell = row.getCell(1); // ObjectId column
                if (cell != null) {
                    String objectId = cell.toString().trim();
                    if (!objectId.isEmpty()) {
                        data.add(new Object[]{objectId});
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //System.out.println("✅ DataProvider loaded " + data.size() + " ObjectIds from Excel.");
        return data.iterator();
    }	
	
	
	
	

}
