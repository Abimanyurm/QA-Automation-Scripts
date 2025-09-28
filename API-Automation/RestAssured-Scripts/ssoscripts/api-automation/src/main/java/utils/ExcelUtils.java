package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;      
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {
	
	private static final String FILE_PATH = System.getProperty("user.dir") + "/src/test/resources/MerchantT24.xlsx";
	private static String filePath_shop = "C:\\Users\\abimanyu.r\\eclipse-workspace\\Automate files\\api-automation\\src\\test\\shoplocation.xlsx";
	private static String sheetName = "UserDetails01";
	public static int writtenCount;
	public static int sNo = 1;

	public static int writeMerchantId(List<String> merchantIds) {
		writtenCount = 0;
		try {
			File file = new File(FILE_PATH);
			XSSFWorkbook workbook;
			XSSFSheet sheet;

			if (file.exists()) {
				FileInputStream fis = new FileInputStream(file);
				workbook = new XSSFWorkbook(fis);
				fis.close();
				sheet = workbook.getSheet("MerchantsT24");
				if (sheet == null) sheet = workbook.createSheet("MerchantsT24");
			} else {
				workbook = new XSSFWorkbook();
				sheet = workbook.createSheet("MerchantsT24");
			}

			int lastRowNum = sheet.getLastRowNum();
			int writeRow = lastRowNum + 1;

			for (String id : merchantIds) {
				Row row = sheet.getRow(writeRow);
				if (row == null) row = sheet.createRow(writeRow);

				Cell cell = row.getCell(0);
				if (cell == null || cell.toString().trim().isEmpty()) {
					cell = row.createCell(0);
					cell.setCellValue(id);
					writtenCount++;
					System.out.println(sNo + " MerchantId written : " + id);
					writeRow++;
				}
			}

			FileOutputStream fos = new FileOutputStream(file);
			workbook.write(fos);
			fos.close();
			workbook.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return writtenCount;
	}

	/**
	 * ✅ Get last N Merchant IDs from Excel
	 */
	public static List<String> getLastNMerchantIds(int n) {
		List<String> ids = new ArrayList<>();
		try (FileInputStream fis = new FileInputStream(FILE_PATH);
		     XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

			XSSFSheet sheet = workbook.getSheet("MerchantsT24");
			if (sheet == null) return ids;

			int lastRow = sheet.getLastRowNum();
			int startRow = Math.max(0, lastRow - n + 1);

			for (int i = startRow; i <= lastRow; i++) {
				Row row = sheet.getRow(i);
				if (row != null) {
					Cell cell = row.getCell(0);
					if (cell != null) ids.add(cell.toString().trim());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ids;
	}

	public static List<Map<String, String>> getData() {
		List<Map<String, String>> dataList = new ArrayList<>();
		try (FileInputStream fis = new FileInputStream(filePath_shop);
		     Workbook workbook = new XSSFWorkbook(fis)) {

			Sheet sheet = workbook.getSheet(sheetName);
			Row headerRow = sheet.getRow(0);

			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				Map<String, String> dataMap = new HashMap<>();

				for (int j = 0; j < row.getLastCellNum(); j++) {
					String key = headerRow.getCell(j).getStringCellValue();
					String value = (row.getCell(j) != null) ? row.getCell(j).toString().trim() : "null";
					if ("null".equalsIgnoreCase(value)) value = "null";
					dataMap.put(key, value);
				}
				dataList.add(dataMap);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataList;
	}

	// ===================== NEW METHODS FOR FLUSHING MIDs =====================

	/**
	 * Delete specific Merchant IDs from Excel after deletion
	 */
	public static void deleteSpecificMerchantIds(List<String> midsToDelete) {
		try (FileInputStream fis = new FileInputStream(FILE_PATH);
		     XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

			XSSFSheet sheet = workbook.getSheet("MerchantsT24");
			if (sheet == null) return;

			List<Integer> rowsToRemove = new ArrayList<>();
			for (int i = 0; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row != null) {
					Cell cell = row.getCell(0);
					if (cell != null && midsToDelete.contains(cell.toString().trim())) {
						rowsToRemove.add(i);
					}
				}
			}

			// Remove rows in reverse order to prevent shifting issues
			for (int i = rowsToRemove.size() - 1; i >= 0; i--) {
				int rowIndex = rowsToRemove.get(i);
				Row row = sheet.getRow(rowIndex);
				if (row != null) {
					sheet.removeRow(row);
				}
			}

			// Write back to file
			FileOutputStream fos = new FileOutputStream(FILE_PATH);
			workbook.write(fos);
			fos.close();
			workbook.close();

			System.out.println("✅ Deleted merchant IDs flushed from Excel: " + midsToDelete);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Clear all Merchant IDs from Excel (full flush)
	 */
	public static void clearMerchantIds() {
		try (FileInputStream fis = new FileInputStream(FILE_PATH);
		     XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

			XSSFSheet sheet = workbook.getSheet("MerchantsT24");
			if (sheet != null) {
				int lastRow = sheet.getLastRowNum();
				for (int i = lastRow; i >= 0; i--) {
					Row row = sheet.getRow(i);
					if (row != null) sheet.removeRow(row);
				}
			}

			FileOutputStream fos = new FileOutputStream(FILE_PATH);
			workbook.write(fos);
			fos.close();
			workbook.close();

			System.out.println("✅ All merchant IDs cleared from Excel");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}