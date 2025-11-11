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
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {

	// Merchant IDs Excel
	private static final String FILE_PATH = System.getProperty("user.dir") + "/src/test/resources/MerchantT24.xlsx";
	private static final String SHEET_NAME = "MerchantsT24";

	// Shop details Excel (absolute path)
	private static final String SHOP_FILE_PATH = "C:\\Users\\abimanyu.r\\eclipse-workspace\\Automate files\\api-automation\\src\\test\\shoplocation.xlsx";
	private static final String SHOP_SHEET_NAME = "UserDetails01";

	public static int sNo = 1;

	// ======= MERCHANT METHODS =======

	public static Map<String, Integer> writeMerchantId(List<String> merchantIds, List<String> types) {
		int posCount = 0;
		int softPosCount = 0;

		try {
			File file = new File(FILE_PATH);
			XSSFWorkbook workbook;
			XSSFSheet sheet;

			if (file.exists()) {
				try (FileInputStream fis = new FileInputStream(file)) {
					workbook = new XSSFWorkbook(fis);
				}
				sheet = workbook.getSheet(SHEET_NAME);
				if (sheet == null)
					sheet = workbook.createSheet(SHEET_NAME);
			} else {
				workbook = new XSSFWorkbook();
				sheet = workbook.createSheet(SHEET_NAME);
			}

			int writeRow = sheet.getLastRowNum() + 1;

			for (int i = 0; i < merchantIds.size(); i++) {
				String id = merchantIds.get(i);
				String type = (types != null && i < types.size() && types.get(i) != null) ? types.get(i).trim() : "POS";

				Row row = sheet.getRow(writeRow);
				if (row == null)
					row = sheet.createRow(writeRow);

				Cell cellId = row.createCell(0);
				cellId.setCellValue(id);

				Cell cellType = row.createCell(1);
				cellType.setCellValue(type);

				if ("softpos".equalsIgnoreCase(type) || "soft pos".equalsIgnoreCase(type))
					softPosCount++;
				else
					posCount++;

				System.out.println(sNo++ + " MID written: " + id + " | Type: " + type);
				writeRow++;
			}

			try (FileOutputStream fos = new FileOutputStream(file)) {
				workbook.write(fos);
			}
			workbook.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, Integer> countMap = new HashMap<>();
		countMap.put("POS", posCount);
		countMap.put("SoftPOS", softPosCount);
		return countMap;
	}

	public static List<String> getLastNMerchantIds(int n) {
		List<String> ids = new ArrayList<>();
		try (FileInputStream fis = new FileInputStream(FILE_PATH); XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

			XSSFSheet sheet = workbook.getSheet(SHEET_NAME);
			if (sheet == null)
				return ids;

			int lastRow = sheet.getLastRowNum();
			int startRow = Math.max(0, lastRow - n + 1);

			for (int i = startRow; i <= lastRow; i++) {
				Row row = sheet.getRow(i);
				if (row != null) {
					Cell cell = row.getCell(0);
					if (cell != null)
						ids.add(cell.toString().trim());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ids;
	}

	public static List<String> getLastNPosMerchantIds(int n) {
		List<String> posIds = new ArrayList<>();
		try (FileInputStream fis = new FileInputStream(FILE_PATH); XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

			XSSFSheet sheet = workbook.getSheet(SHEET_NAME);
			if (sheet == null)
				return posIds;

			int lastRow = sheet.getLastRowNum();
			for (int i = lastRow; i >= 0 && posIds.size() < n; i--) {
				Row row = sheet.getRow(i);
				if (row != null) {
					Cell typeCell = row.getCell(1);
					Cell idCell = row.getCell(0);
					if (typeCell != null && idCell != null && !typeCell.toString().toLowerCase().contains("soft")) {
						posIds.add(0, idCell.toString().trim());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return posIds;
	}

	public static List<String> getLastNSoftPosMerchantIds(int n) {
		List<String> softPosIds = new ArrayList<>();
		try (FileInputStream fis = new FileInputStream(FILE_PATH); XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

			XSSFSheet sheet = workbook.getSheet(SHEET_NAME);
			if (sheet == null)
				return softPosIds;

			int lastRow = sheet.getLastRowNum();
			for (int i = lastRow; i >= 0 && softPosIds.size() < n; i--) {
				Row row = sheet.getRow(i);
				if (row != null) {
					Cell typeCell = row.getCell(1);
					Cell idCell = row.getCell(0);
					if (typeCell != null && idCell != null && typeCell.toString().toLowerCase().contains("soft")) {
						softPosIds.add(0, idCell.toString().trim());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return softPosIds;
	}

	public static void deleteSpecificMerchantIds(List<String> midsToDelete) {
		try (FileInputStream fis = new FileInputStream(FILE_PATH); XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

			XSSFSheet sheet = workbook.getSheet(SHEET_NAME);
			if (sheet == null)
				return;

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

			for (int i = rowsToRemove.size() - 1; i >= 0; i--) {
				Row row = sheet.getRow(rowsToRemove.get(i));
				if (row != null)
					sheet.removeRow(row);
			}

			try (FileOutputStream fos = new FileOutputStream(FILE_PATH)) {
				workbook.write(fos);
			}
			workbook.close();

			System.out.println("✅ Deleted merchant IDs flushed from Excel: " + midsToDelete);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void clearMerchantIds() {
		try (FileInputStream fis = new FileInputStream(FILE_PATH); XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

			XSSFSheet sheet = workbook.getSheet(SHEET_NAME);
			if (sheet != null) {
				for (int i = sheet.getLastRowNum(); i >= 0; i--) {
					Row row = sheet.getRow(i);
					if (row != null)
						sheet.removeRow(row);
				}
			}

			try (FileOutputStream fos = new FileOutputStream(FILE_PATH)) {
				workbook.write(fos);
			}
			workbook.close();

			System.out.println("✅ All merchant IDs cleared from Excel");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
   
	
	
	// ======= SHOP METHODS =======

	public static List<Map<String, String>> getData() {
		List<Map<String, String>> dataList = new ArrayList<>();
		try {
			File file = new File(SHOP_FILE_PATH);
			if (!file.exists()) {
				System.err.println("❌ Shop Excel file not found at: " + SHOP_FILE_PATH);
				return dataList;
			}

			try (FileInputStream fis = new FileInputStream(file); XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

				XSSFSheet sheet = workbook.getSheet(SHOP_SHEET_NAME);
				if (sheet == null) {
					System.err.println("❌ Sheet not found: " + SHOP_SHEET_NAME);
					return dataList;
				}

				Row headerRow = sheet.getRow(0);
				if (headerRow == null)
					return dataList;

				for (int i = 1; i <= sheet.getLastRowNum(); i++) {
					Row row = sheet.getRow(i);
					if (row == null)
						continue;

					Map<String, String> dataMap = new HashMap<>();
					for (int j = 0; j < headerRow.getLastCellNum(); j++) {
						Cell keyCell = headerRow.getCell(j);
						String key = (keyCell != null) ? keyCell.getStringCellValue() : "Column" + j;
						Cell valueCell = row.getCell(j);
						String value = (valueCell != null) ? valueCell.toString().trim() : "null";
						dataMap.put(key, value);
					}
					dataList.add(dataMap);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataList;
	}
	//-------------->For split Pos and softpos request in same sheet<--------------//
	public static List<Map<String, String>> getShopDataByType(String type) {
	    List<Map<String, String>> allData = getData();
	    List<Map<String, String>> filtered = new ArrayList<>();

	    for (Map<String, String> row : allData) {
	        String merchantType = row.get("merchantType"); // First column
	        if (merchantType != null && merchantType.equalsIgnoreCase(type)) {
	            filtered.add(row);
	        }
	    }
	    return filtered;
	}
}