package utils;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.testng.annotations.DataProvider;
import config.configManager;
import payloads.DeleteShopPayload;
import payloads.GlobalPayloads;
import payloads.ShopDetailsPayload;

public class TestDataProvider {

	static int numberOfMerchants;

	@DataProvider(name = "accountData")
	public static Object[][] accountData() {
		// Define all possible test cases
		return new Object[][] {
				{ configManager.getProperty("account.value1"), configManager.getProperty("account.value2"), null,
						"success", "Valid Account", false },
				{ configManager.getProperty("account.valuein1"), configManager.getProperty("account.value2"), null,
						"failed", "Invalid Account No", false } };
	}

	// Add other cases like before...

	@DataProvider(name = "channelData")
	public static Object[][] channelData() {
		return new Object[][] {
				// { "C02", true, "SOFTPOS Channel - valid", "success" },
				// { "C09", true, "Invalid Channel", "failed" },
				{ "C01", true, "POS Channel - valid", "success" } };
	}

	@DataProvider(name = "kycData")
	public static Object[][] kycData() {
		return new Object[][] {

				{ configManager.getProperty("account.gmidin"), configManager.getProperty("Invalid.commerceFile"),
						configManager.getProperty("commerceNumber"),
						Integer.parseInt(configManager.getProperty("LocationValid")),
						configManager.getProperty("Invalid.passport"), configManager.getProperty("Invalid.passportNo"),
						"failed", "Invalid KYC", false },
				{ configManager.getProperty("latest.gmid"), configManager.getProperty("valid.commerceFile"),
						configManager.getProperty("commerceNumber"),
						Integer.parseInt(configManager.getProperty("LocationValid")),
						configManager.getProperty("valid.passport"), configManager.getProperty("valid.passportNo"),
						"success", "Valid KYC", true }

		};
	}

	@DataProvider(name = "kycDatas")
	public static Object[][] kycData1() {
		return new Object[][] {

				{ configManager.getProperty("latest.gmid"), configManager.getProperty("valid.commerceFile"),
						configManager.getProperty("commerceNumber"),
						Integer.parseInt(configManager.getProperty("LocationValid")),
						configManager.getProperty("valid.passport"), configManager.getProperty("valid.passportNo"),
						"success", "Valid KYC", true }

		};
	}

	@DataProvider(name = "merchantData")
	public static Object[][] merchantData() {
		// Define all possible test cases
		return new Object[][] { { configManager.getProperty("account.value1"),
				configManager.getProperty("account.value2"), null, "200", "Details", false },

		};

	}

	@DataProvider(name = "shopDetailsData")
	public static Object[][] shopDetailsData() throws InterruptedException {

		List<Map<String, String>> excelData = ExcelUtils.getData();

		// 🔹 Step 2: Ask user how many MIDs to use
		Scanner sc = new Scanner(System.in);
		System.out.print("\nEnter number of MIDs to update: ");
		int n = sc.nextInt();

		// Step 3: Fetch last N merchant IDs from MerchantT24.xlsx
		List<String> mids = ExcelUtils.getLastNMerchantIds(n);

		Object[][] data = new Object[excelData.size() * mids.size()][1];
		int index = 0;
		for (Map<String, String> row : excelData) {
			for (String mid : mids) {

				// Map Excel → POJO
				ShopDetailsPayload payload = new ShopDetailsPayload();
				ShopDetailsPayload.ShopDetails shop = new ShopDetailsPayload.ShopDetails();

				shop.setLocation(row.get("location"));
				shop.setAddress(row.get("address"));
				shop.setArea(row.get("area"));
				shop.setCity(row.get("city"));
				shop.setState(row.get("state"));
				shop.setPostalCode((int) Double.parseDouble(row.get("postalCode")));
				// shop.setPostalCode(row.get("postalCode"));
				shop.setNoOfPOSDevices((int) Double.parseDouble(row.get("noOfPOSDevices")));
				shop.setSoftPOS((int) Double.parseDouble(row.get("softPOS")));
				shop.setSupervisorNumber(row.get("supervisorNumber"));
				shop.setLocationMunicipalityFile(row.get("locationMunicipalityFile"));
				shop.setBizCategoryName(row.get("bizCategoryName"));
				shop.setBizCategoryId(row.get("bizCategoryId"));
				shop.setBussinessAddress(row.get("bussinessAddress"));
				shop.setPhotoOfThePermisesFile(row.get("photoOfThePermisesFile"));
				shop.setSignboardFile(row.get("signboardFile"));
				shop.setCounty(row.get("county"));
				shop.setRegion(row.get("region"));

				String terminals = row.get("terminalNumbers");
				if (terminals != null && !terminals.isEmpty()) {
					shop.setTerminalNumbers(Arrays.asList(terminals.split(",")));
				} else {
					shop.setTerminalNumbers(Collections.emptyList()); // ensures "terminalNumbers": []
				}

				String inputsStr = row.get("inputs");
				if (inputsStr != null && !inputsStr.isEmpty()) {
					shop.setInputs(Arrays.asList(inputsStr.split(",")));
				} else {
					shop.setInputs(Collections.emptyList()); // ✅ ensures "inputs": []
				}
				shop.setMcc(row.get("mcc"));
				shop.setMobile(row.get("mobile"));
				shop.setPhone(row.get("phone"));
				shop.setIsPg(Boolean.parseBoolean(row.get("isPg")));
				shop.setSeamlessIntegration(Boolean.parseBoolean(row.get("seamlessIntegration")));
				shop.setSiMid(row.get("siMid"));
				shop.setLastName(row.get("lastName"));
				shop.setPobox((int) Double.parseDouble(row.get("pobox")));
				// shop.setPobox(row.get("pobox"));
				shop.setFirstName(row.get("firstName"));
				shop.setEmail(row.get("email"));
				shop.setEntityName(row.get("entityName"));

				shop.setMerchantBusinessNature(row.get("merchantBusinessNature"));

				shop.setContactPerson(row.get("contactPerson"));
				shop.setContactPersonMobile(row.get("contactPersonMobile"));
				shop.setBizWebsite(row.get("bizWebsite"));
				shop.setBusiness(row.get("business"));
				String refundAllow = row.get("transactionRefundAllow");
				if (refundAllow != null && !refundAllow.trim().isEmpty()) {
					shop.setTransactionRefundAllow(Boolean.valueOf(refundAllow));
				} else {
					shop.setTransactionRefundAllow(null); // keep it empty
				}

				shop.setBinSupported(row.get("binSupported"));

				// BusinessVolume nested
				ShopDetailsPayload.ShopDetails.BusinessVolume bv = new ShopDetailsPayload.ShopDetails.BusinessVolume();
				bv.setMin((int) Double.parseDouble(row.get("businessVolumeMin")));
				bv.setMax((int) Double.parseDouble(row.get("businessVolumeMax")));
				shop.setBusinessVolume(bv);

				shop.setCommercialName(row.get("commercialName"));
				shop.setFirstName(row.get("firstName"));
				shop.setLastName(row.get("lastName"));

				shop.setLatitude(Double.parseDouble(row.get("latitude")));
				shop.setLongitude(Double.parseDouble(row.get("longitude")));

				payload.setShopDetails(shop);
				// ---- attach merchantId from lastN fetched IDs ----
				payload.setMerchantId(mid);
				
				System.out.println(mid);

				data[index++][0] = payload;
			}
                Thread.sleep(2000);
		}
		return data;

	}
	
	@DataProvider(name = "deleteShopData")
	public static Object[][] deleteShopData() {

	    Scanner sc = new Scanner(System.in);

	    // ✅ Step 1: Check if merchants exist
	    int writtenCount = ExcelUtils.writtenCount;
	    if (writtenCount == 0) {
	        System.out.println("⚠️ No merchants available to delete.");
	        return new Object[0][0];
	    }

	    // ✅ Step 2: Ask how many merchants to delete
	    System.out.print("Enter how many merchants you want to delete: ");
	    int deleteCount = sc.nextInt();

	    // ✅ Step 3: Validate input
	    if (deleteCount > writtenCount) {
	        System.out.println("⚠️ You requested more than available. Deleting ALL merchants instead.");
	        deleteCount = writtenCount;
	    }

	    // ✅ Step 4: Fetch last N merchants from Excel
	    List<String> midsToDelete = ExcelUtils.getLastNMerchantIds(deleteCount);

	    // ✅ Step 5: Prepare DataProvider payload
	    Object[][] data = new Object[midsToDelete.size()][1];
	    for (int i = 0; i < midsToDelete.size(); i++) {
	        data[i][0] = new DeleteShopPayload(true, midsToDelete.get(i));
	    }

	    // ✅ Step 6: Log summary
	    System.out.println("\n===========================================");
	    System.out.println("✅ Merchants selected for deletion: " + midsToDelete);
	    System.out.println("===========================================");
	    
	    
	    // ✅ Step 7: Remove deleted MIDs from Excel immediately
	    ExcelUtils.deleteSpecificMerchantIds(midsToDelete);
	    System.out.println("✅ Deleted merchant IDs flushed from Excel.");

	    return data;
	}

	@DataProvider(name = "confirmPageData")
	public static Object[][] confirmPageData() {
		return new Object[][] {

				{ config.configManager.getProperty("confirmPage.key3"), "Confirm Page TC - enabled", "success" },
				{ config.configManager.getProperty("confirmPage.key4"), "Confirm Page TC - disabled", "failed" }};
	}

	@DataProvider(name = "otpData")
	public static Object[][] otpData() {
		return new Object[][] {
				{ config.configManager.getProperty("otpw"), config.configManager.getProperty("otpFrom"),
						"Invalid OTP Test", "failed" },
				{ config.configManager.getProperty("otp"), config.configManager.getProperty("otpFrom"),
						"Valid OTP Test", "success" }

		};
	}

	@DataProvider(name = "submitPageData")
	public static Object[][] submitPageData() {
		return new Object[][] { { "false", "Submit Page Disabled Test", "failed" },
				{ "true", "Submit Page Enabled Test", "success" }

		};
	}

	/**
	 * Helper to fetch one populated ShopDetailsPayload (first Excel+MID combo) so
	 * that cross-case tests can reuse the same Payload without a DataProvider.
	 */
	public static ShopDetailsPayload getSingleShopDetailsPayload() {
		try {
			Object[][] allData = shopDetailsData();
			if (allData != null && allData.length > 0 && allData[0].length > 0) {
				return (ShopDetailsPayload) allData[0][0];
			} else {
				throw new RuntimeException("shopDetailsData returned no rows. Check Excel or input source.");
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to load ShopDetailsPayload from DataProvider", e);
		}
	}
	
	// Add these methods at the bottom of TestDataProvider class

    /**
     * Helper to fetch one valid Account record
     */
    public static Object[] getSingleAccountData() {
        Object[][] data = accountData();
        if (data != null && data.length > 0) {
            return data[0];   // returns first row as Object[]
        }
        throw new RuntimeException("No account data available");
    }

    /**
     * Helper to fetch one valid Channel record
     */
    public static Object[] getSingleChannelData() {
        Object[][] data = channelData();
        if (data != null && data.length > 0) {
            return data[0];
        }
        throw new RuntimeException("No channel data available");
    }

    /**
     * Helper to fetch one valid KYC record
     */
    public static Object[] getSingleKycData() {
        Object[][] data = kycData();
        if (data != null && data.length > 0) {
            return data[0];
        }
        throw new RuntimeException("No KYC data available");
    }

    /**
     * Helper to fetch one valid Merchant record
     */
    public static GlobalPayloads getSingleMerchantData() {
    	Object[][] data = merchantData();
        if (data != null && data.length > 0) {
            Object[] row = data[0];
            GlobalPayloads payload = new GlobalPayloads();
            payload.setAccountNo((String) row[0]);
            payload.setCifNumber((String) row[1]);
            // set other fields if required
            return payload; 
        }
        throw new RuntimeException("No merchant data available");
    }

    /**
     * Helper to fetch one valid Confirm Page record
     */
    public static Object[] getSingleConfirmPageData() {
        Object[][] data = confirmPageData();
        if (data != null && data.length > 0) {
            return data[0];
        }
        throw new RuntimeException("No confirm page data available");
    }

    /**
     * Helper to fetch one valid OTP record
     */
    public static Object[] getSingleOtpData() {
        Object[][] data = otpData();
        if (data != null && data.length > 0) {
            return data[0];
        }
        throw new RuntimeException("No OTP data available");
    }

    /**
     * Helper to fetch one valid Submit Page record
     */
    public static Object[] getSingleSubmitPageData() {
        Object[][] data = submitPageData();
        if (data != null && data.length > 0) {
            return data[0];
        }
        throw new RuntimeException("No submit page data available");
    }
	
	
	

}
